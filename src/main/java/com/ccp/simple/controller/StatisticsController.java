package com.ccp.simple.controller;

import com.ccp.simple.aop.LogActivity;
import com.ccp.simple.dto.KeywordCountDto;
import com.ccp.simple.dto.NewsResponseDto;
import com.ccp.simple.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @LogActivity("인기 뉴스 통계 조회 (관리자)")
    @GetMapping("/topLiked")
    public List<NewsResponseDto> getTopLikedNews(@RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getTopLikedNews(limit);
    }

    @LogActivity("조회수 많은 뉴스 통계 조회 (관리자)")
    @GetMapping("/topViewed")
    public List<NewsResponseDto> getTopViewedNews(@RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getTopViewedNews(limit);
    }

    @LogActivity("키워드별 좋아요 통계 조회 (관리자)")
    @GetMapping("/totalLikesByKeyword")
    public List<KeywordCountDto> getTotalLikesByKeyword(@RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getTotalLikesByKeyword(limit);
    }
}
