package com.ccp.simple.service;

import com.ccp.simple.document.NewsDocument;
import com.ccp.simple.domain.Keyword;
import com.ccp.simple.dto.NewsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface NewsService {
    List<NewsResponseDto> getAllNews();

    void insertNews(String responseBody, Long keywordId) throws JsonProcessingException;

    void updateNewsVisibility(Long newsId, boolean visible);

    boolean toggleLike(String userId, Long newsId);

    void updateLikeCount(Long newsId, int likeCount);

    List<NewsDocument> searchNews(String query);

    void insertKeyword(Keyword keyword);

    List<Keyword> getAllKeywords();

    void deleteKeyword(int keywordId);
}
