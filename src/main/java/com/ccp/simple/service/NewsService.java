package com.ccp.simple.service;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.dto.NewsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface NewsService {
    List<NewsResponseDto> getAllNews();

    void insertNews(String responseBody, Long keywordId) throws JsonProcessingException;

    void insertKeyword(Keyword keyword);

    List<Keyword> getAllKeywords();

    void deleteKeyword(int keywordId);
}
