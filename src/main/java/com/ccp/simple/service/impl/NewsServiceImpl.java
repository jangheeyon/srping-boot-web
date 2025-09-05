package com.ccp.simple.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsMapper newsMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final NewsSearchRepository newsSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<NewsResponseDto> getAllNews() {
        String userId = checkUserId();
        List<NewsResponseDto> newsList = newsMapper.getAllNews();
        matchEsResultsWithRedisData(newsList, userId);
        return newsList;
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
        Query esQuery = new Query.Builder()
                //mmq : multi_match
                .multiMatch(mmq -> mmq
                        .fields("title", "description")
                        .query(query)
                )
                .build();
        return searchElasticsearch(esQuery);
    }

    @Override
    public List<NewsResponseDto> searchSimilarNews(Long newsId) {
        Query esQuery = new Query.Builder()
                .moreLikeThis(mlt -> mlt
                        .fields("title", "description")
                        .like(l -> l.document(d -> d.index("news").id(String.valueOf(newsId))))
                        .minTermFreq(1)
                        .minDocFreq(1)
                )
                .build();
        return searchElasticsearch(esQuery);
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
            // 좋아요 취소
            redisTemplate.opsForSet().remove(likeKey, userId);
            newsMapper.deleteLike(userId, newsId);
            return false;
        } else {
            // 좋아요 추가
            redisTemplate.opsForSet().add(likeKey, userId);
            newsMapper.insertLike(userId, newsId);
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

    //유저 확인
    private String checkUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    //좋아요 수 매칭
    private void matchEsResultsWithRedisData(List<NewsResponseDto> newsList, String userId) {
        for (NewsResponseDto news : newsList) {
            String likeKey = "news:like:" + news.getNewsId();
            Long likeCount = redisTemplate.opsForSet().size(likeKey);
            news.setLikeCount(likeCount != null ? likeCount.intValue() : 0);

            if (userId != null) {
                news.setLiked(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likeKey, userId)));
            } else {
                news.setLiked(false);
            }
        }
    }

    //엘라스틱서치 검색
    private List<NewsResponseDto> searchElasticsearch(Query esQuery) {
        NativeQuery nativeQuery = new NativeQueryBuilder().withQuery(esQuery).build();
        SearchHits<NewsDocument> searchHits = elasticsearchOperations.search(nativeQuery, NewsDocument.class);

        List<Long> newsIds = new ArrayList<>();
        //searchHits에서 newsIds를 반환
        for(SearchHit<NewsDocument> hit : searchHits.getSearchHits()) {
            NewsDocument content = hit.getContent();
            newsIds.add(content.getNewsId());
        }
        return orderResults(newsIds);
    }

    // 결과 유사도 정렬 유지
    private List<NewsResponseDto> orderResults(List<Long> newsIds) {
        if (newsIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, NewsResponseDto> newsDtoMap = new HashMap<>();
        List<NewsResponseDto> newsByIds = newsMapper.findNewsByIds(newsIds);

        for (NewsResponseDto newsDto : newsByIds) {
            newsDtoMap.put(newsDto.getNewsId(), newsDto);
        }

        List<NewsResponseDto> newsList = (List<NewsResponseDto>) newsDtoMap.values();
        String userId = checkUserId();
        matchEsResultsWithRedisData(newsList, userId);

        List<NewsResponseDto> resultList = new ArrayList<>();
        //검색결과 유사도 정렬 유지
        for (Long newsId : newsIds) {
            NewsResponseDto newsDto = newsDtoMap.get(newsId);
            if (newsDto != null) {
                resultList.add(newsDto);
            }
        }
        return resultList;
    }
}
