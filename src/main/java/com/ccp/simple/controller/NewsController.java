package com.ccp.simple.controller;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.dto.NewsResponseDto;
import com.ccp.simple.dto.RegisterKeywordRequestDto;
import com.ccp.simple.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // (관리자) 뉴스 숨김/공개 처리
    @PatchMapping("/admin/news/{newsId}")
    public ResponseEntity<String> updateNewsVisibility(@PathVariable Long newsId, @RequestBody Map<String, Boolean> payload) {
        Boolean visible = payload.get("visible");
        if (visible == null) {
            return ResponseEntity.badRequest().body("\"visible\" 필드가 필요합니다.");
        }
        newsService.updateNewsVisibility(newsId, visible);
        return ResponseEntity.ok("뉴스 상태가 변경되었습니다.");
    }

    // 뉴스 좋아요 토글
    @PostMapping("/news/{newsId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long newsId, Authentication authentication) {
        String userId = authentication.getName(); // 현재 로그인한 사용자 ID
        boolean isLiked = newsService.toggleLike(userId, newsId);

        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", isLiked);
        response.put("message", isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.");

        return ResponseEntity.ok(response);
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
