package com.ccp.simple.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.ccp.simple.document.NewsDocument;
import com.ccp.simple.domain.Keyword;
import com.ccp.simple.domain.News;
import com.ccp.simple.dto.NewsResponseDto;
import com.ccp.simple.mapper.NewsMapper;
import com.ccp.simple.repository.NewsSearchRepository;
import com.ccp.simple.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsMapper newsMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final NewsSearchRepository newsSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<NewsResponseDto> getAllNews() {
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            userId = authentication.getName();
        }

        List<NewsResponseDto> newsList = newsMapper.getAllNews();

        for (NewsResponseDto news : newsList) {
            String likeKey = "news:like:" + news.getNewsId();

            // Redis에서 실시간 좋아요 개수 가져오기
            Long likeCount = redisTemplate.opsForSet().size(likeKey);
            news.setLikeCount(likeCount != null ? likeCount.intValue() : 0);

            // 현재 사용자가 좋아요를 눌렀는지 확인
            if (userId != null) {
                Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
                news.setLiked(Boolean.TRUE.equals(isMember));
            } else {
                news.setLiked(false); // 로그인하지 않은 사용자는 항상 false
            }
        }
        return newsList;
    }

    @Override
    @Transactional
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
                // 1. DB에 뉴스 저장
                newsMapper.insertNews(news);
                newsId = news.getNewsId();

                // 2. Elasticsearch에 뉴스 인덱싱
                NewsDocument newsDocument = NewsDocument.builder()
                        .newsId(newsId)
                        .title(news.getTitle())
                        .description(news.getDescription())
                        .link(news.getLink())
                        .pubDt(news.getPubDt())
                        .build();
                newsSearchRepository.save(newsDocument);

            } else {
                newsId = newsMapper.getNewsIdByLink(link);
            }
            newsMapper.insertNewsKeywordMapping(newsId, keywordId);
        }
    }

    @Override
    public List<NewsResponseDto> searchNews(String query) {
        // 1. Elasticsearch에서 관련도 순으로 정렬된 NewsDocument 검색
        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(q -> q.multiMatch(mmq -> mmq.fields("title", "description").query(query)))
                .build();
        SearchHits<NewsDocument> searchHits = elasticsearchOperations.search(nativeQuery, NewsDocument.class);

        // 2. 검색 결과에서 newsId 목록을 '관련도 순서대로' 추출
        List<Long> newsIds = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(NewsDocument::getNewsId)
                .collect(Collectors.toList());

        if (newsIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 추출된 ID 목록을 사용하여 DB에서 뉴스 정보를 한 번에 조회 (N+1 문제 방지)
        Map<Long, NewsResponseDto> newsDtoMap = newsMapper.findNewsByIds(newsIds).stream()
                .collect(Collectors.toMap(NewsResponseDto::getNewsId, Function.identity()));

        // 4. Redis에서 실시간 좋아요 정보 보강
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            userId = authentication.getName();
        }

        for (NewsResponseDto news : newsDtoMap.values()) {
            String likeKey = "news:like:" + news.getNewsId();
            Long likeCount = redisTemplate.opsForSet().size(likeKey);
            news.setLikeCount(likeCount != null ? likeCount.intValue() : 0);

            if (userId != null) {
                news.setLiked(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likeKey, userId)));
            } else {
                news.setLiked(false);
            }
        }

        // 5. Elasticsearch의 '관련도 순서'를 유지하면서 최종 데이터 조립
        return newsIds.stream()
                .map(newsDtoMap::get)
                .collect(Collectors.toList());
    }

    @Override
    public void updateNewsVisibility(Long newsId, boolean visible) {
        newsMapper.updateNewsVisibility(newsId, visible);
    }

    @Override
    @Transactional
    public boolean toggleLike(String userId, Long newsId) {
        String likeKey = "news:like:" + newsId;
        Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);

        if (Boolean.TRUE.equals(isMember)) {
            // Redis 좋아요 취소
            redisTemplate.opsForSet().remove(likeKey, userId);
            // newsMapper.deleteLike(userId, newsId);

            String countKey = "news:like_count:" + newsId;
            redisTemplate.opsForValue().decrement(countKey);

            return false;
        } else {
            // Redis 좋아요 추가
            redisTemplate.opsForSet().add(likeKey, userId);
            // newsMapper.insertLike(userId, newsId);

            String countKey = "news:like_count:" + newsId;
            redisTemplate.opsForValue().increment(countKey);

            return true;
        }
    }

    @Override
    public void updateLikeCount(Long newsId, int likeCount) {
        newsMapper.updateLikeCount(newsId, likeCount);
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
