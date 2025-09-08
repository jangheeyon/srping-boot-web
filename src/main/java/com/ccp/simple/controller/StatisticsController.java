package com.ccp.simple.controller;

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

    @GetMapping("/topLiked")
    public List<NewsResponseDto> getTopLikedNews(@RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getTopLikedNews(limit);
    }

    @GetMapping("/topViewed")
    public List<NewsResponseDto> getTopViewedNews(@RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getTopViewedNews(limit);
    }
}
