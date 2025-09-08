package com.ccp.simple.service.impl;

import com.ccp.simple.dto.KeywordCountDto;
import com.ccp.simple.dto.NewsResponseDto;
import com.ccp.simple.mapper.NewsMapper;
import com.ccp.simple.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final NewsMapper newsMapper;

    @Override
    public List<NewsResponseDto> getTopLikedNews(int limit) {
        return newsMapper.getTopLikedNews(limit);
    }

    @Override
    public List<NewsResponseDto> getTopViewedNews(int limit) {
        return newsMapper.getTopViewedNews(limit);
    }

    @Override
    public List<KeywordCountDto> getTotalLikesByKeyword(int limit) {
        return newsMapper.getTotalLikesByKeyword(limit);
    }
}
