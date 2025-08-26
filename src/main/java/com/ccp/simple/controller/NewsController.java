package com.ccp.simple.controller;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.dto.NewsResponseDto;
import com.ccp.simple.dto.RegisterKeywordRequestDto;
import com.ccp.simple.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    //뉴스 수집
    @GetMapping("/news")
    public List<NewsResponseDto> getAllNews() {
        return newsService.getAllNews();
    }

    //키워드 등록
    @PostMapping("/news/keyword")
    public ResponseEntity<String> insertNewsKeyword(@RequestBody RegisterKeywordRequestDto dto) {
        Keyword keyword = new Keyword();
        keyword.setKeyword(dto.getKeyword());
        keyword.setUserId(dto.getUserId());
        newsService.insertKeyword(keyword);
        return ResponseEntity.ok("키워드 등록 성공");
    }

    //키워드 목록 조회
    @GetMapping("/news/keyword")
    public List<Keyword> getAllKeywords() {
        return newsService.getAllKeywords();
    }

    //키워드 삭제
    @DeleteMapping("/news/keyword/{KeywordId}")
    public void deleteKeyword(@PathVariable int KeywordId) {
        newsService.deleteKeyword(KeywordId);
    }

}
