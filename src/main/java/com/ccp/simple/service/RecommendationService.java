package com.ccp.simple.service;

import com.ccp.simple.dto.NewsResponseDto;

import java.util.List;

public interface RecommendationService {

    List<NewsResponseDto> recommendNewsBySimilarUsers(String userId, int limit);
}
