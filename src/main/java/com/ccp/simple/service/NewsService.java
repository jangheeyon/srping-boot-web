package com.ccp.simple.service;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.dto.NewsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface NewsService {
    List<NewsResponseDto> getAllNews();

    List<NewsResponseDto> getAllSubcribedNews();

    void insertNews(String responseBody, Long keywordId) throws JsonProcessingException;

    void updateNewsVisibility(Long newsId, boolean visible);

    boolean toggleLike(String userId, Long newsId);

    void updateLikeCount(Long newsId, int likeCount);

    void incrementViewCount(Long newsId);

    void updateViewCount(Long newsId, int viewCount);

    List<NewsResponseDto> searchNews(String query);

    List<NewsResponseDto> searchSimilarNews(Long newsId);

    List<Keyword> getAllKeywords();

    void subscribeToKeyword(String userId, String keyword);

    List<Keyword> getKeywordsByUserId(String userId);

    void unsubscribeFromKeyword(String userId, Long keywordId);
}
