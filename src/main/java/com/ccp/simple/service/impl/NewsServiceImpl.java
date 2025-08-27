package com.ccp.simple.service.impl;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.domain.News;
import com.ccp.simple.dto.NewsResponseDto;
import com.ccp.simple.mapper.NewsMapper;
import com.ccp.simple.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsMapper newsMapper;


    @Override
    public List<NewsResponseDto> getAllNews() {
        return newsMapper.getAllNews();
    }

    @Override
    public void insertNews(String responseBody, Long keywordId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode itemList = objectMapper.readTree(responseBody).get("items");
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        for (JsonNode item : itemList) {
            String link = item.get("link").asText();
            // 뉴스 중복 체크
            Long newsId;
            if (!newsMapper.existsByLink(link)) {
                News news = new News();
                news.setTitle(Jsoup.parse(item.get("title").asText()).text());
                news.setDescription(Jsoup.parse(item.get("description").asText()).text());
                news.setLink(link);

                String pubDateStr = item.get("pubDate").asText();
                news.setPubDt(ZonedDateTime.parse(pubDateStr, formatter).toLocalDateTime());
                // 뉴스 저장
                newsMapper.insertNews(news);
                newsId = news.getNewsId();
            } else {
                newsId = newsMapper.getNewsIdByLink(link);
            }
            newsMapper.insertNewsKeywordMapping(newsId, keywordId);
        }
    }

    @Override
    public void updateNewsVisibility(Long newsId, boolean visible) {
        newsMapper.updateNewsVisibility(newsId, visible);
    }

    @Override
    public void insertKeyword(Keyword keyword) {
        newsMapper.insertKeyword(keyword);
    }

    @Override
    public List<Keyword> getAllKeywords() {
        return  newsMapper.getAllKeywords();
    }

    @Override
    public void deleteKeyword(int keywordId) {
        newsMapper.deleteKeyword(keywordId);
    }
}
