package com.ccp.simple.scheduler;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NewsCollectScheduler {
    private final NewsService newsService;

//    @Scheduled(cron = "0 */2 0 * * *")  // 매주 월요일 00:00에 실행
    public void collectNews() throws JsonProcessingException {
        String clientId = "DO2YnEI8qqODmwZOYP8N"; //애플리케이션 클라이언트 아이디
        String clientSecret = "6ilVZfSoiE"; //애플리케이션 클라이언트 시크릿

        List<Keyword> allKeywords = newsService.getAllKeywords();
        if (allKeywords.isEmpty()) {
            throw new RuntimeException("검색어가 비어 있습니다.");
        }

        for (Keyword keyword : allKeywords) {
            String searchKeyword = keyword.getKeyword();

            if (searchKeyword == null || searchKeyword.isEmpty()) {
                continue;
            }

            try {
                String encodeQuery = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
                String apiURL = "https://openapi.naver.com/v1/search/news?query=" + encodeQuery;

                Map<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("X-Naver-Client-Id", clientId);
                requestHeaders.put("X-Naver-Client-Secret", clientSecret);

                String responseBody = get(apiURL, requestHeaders);

                newsService.insertNews(responseBody, keyword.getKeywordId());

                // API 호출 간 1~2초 지연 (호출 제한 방지)
                Thread.sleep(1500);

            } catch (Exception e) {
                System.err.println("[" + searchKeyword + "] 뉴스 수집 중 오류 발생: " + e.getMessage());
            }
        }
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);
        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}
