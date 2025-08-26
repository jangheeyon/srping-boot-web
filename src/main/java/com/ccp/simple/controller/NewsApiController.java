package com.ccp.simple.controller;

import com.ccp.simple.domain.News;
import com.ccp.simple.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NewsApiController {

    private final NewsService newsService;

    @GetMapping("/news")
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }
}
