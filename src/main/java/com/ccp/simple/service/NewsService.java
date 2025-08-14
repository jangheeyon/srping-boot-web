package com.ccp.simple.service;

import com.ccp.simple.domain.News;
import com.ccp.simple.dto.RegistNewsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface NewsService {
    List<News> getAllNews();

    void insertNews(String responseBody) throws JsonProcessingException;
}
