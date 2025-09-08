package com.ccp.simple.service;

import com.ccp.simple.dto.NewsResponseDto;

import java.util.List;

public interface StatisticsService {

    List<NewsResponseDto> getTopLikedNews(int limit);

    List<NewsResponseDto> getTopViewedNews(int limit);

}
