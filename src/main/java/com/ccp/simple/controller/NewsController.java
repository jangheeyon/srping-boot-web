package com.ccp.simple.controller;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.dto.NewsResponseDto;
import com.ccp.simple.dto.RegisterKeywordRequestDto;
import com.ccp.simple.scheduler.NewsCollectScheduler;
import com.ccp.simple.service.NewsService;
import com.ccp.simple.service.RecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final RecommendationService recommendationService;
    private final NewsCollectScheduler newsCollectScheduler;

    // 뉴스 목록 조회
    @GetMapping("/news")
    public List<NewsResponseDto> getAllNews() {
        return newsService.getAllNews();
    }

    // 구독 뉴스 목록 조회
    @GetMapping("/news/subscribe")
    public List<NewsResponseDto> getAllSubcribedNews() {
        return newsService.getAllSubcribedNews();
    }

    // 뉴스 검색
    @GetMapping("/news/search")
    public List<NewsResponseDto> searchNews(@RequestParam("q") String query) {
        return newsService.searchNews(query);
    }

    // 유사 뉴스 추천
    @GetMapping("/news/{newsId}/similar")
    public List<NewsResponseDto> searchSimilarNews(@PathVariable Long newsId) {
        return newsService.searchSimilarNews(newsId);
    }

    // 개인화 추천
    @GetMapping("/news/recommend")
    public List<NewsResponseDto> getRecommendations(Authentication authentication) {
        String userId = authentication.getName();
        return recommendationService.recommendNewsBySimilarUsers(userId, 10); // 상위 10개 추천
    }

    // 뉴스 조회수 증가
    @PostMapping("/news/{newsId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long newsId) {
        newsService.incrementViewCount(newsId);
        return ResponseEntity.ok().build();
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
        boolean liked = newsService.toggleLike(userId, newsId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("message", liked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.");

        return ResponseEntity.ok(response);
    }

    //키워드 등록
    @PostMapping("/news/keyword")
    public ResponseEntity<String> subscribeToKeyword(@RequestBody RegisterKeywordRequestDto dto) {
        newsService.subscribeToKeyword(dto.getUserId(), dto.getKeyword());
        return ResponseEntity.ok("키워드를 구독했습니다.");
    }

    @GetMapping("/news/keyword/{userId}")
    public List<Keyword> getMyKeywords(@PathVariable String userId) {
        return newsService.getKeywordsByUserId(userId);
    }

    @DeleteMapping("/news/keyword/{keywordId}")
    public ResponseEntity<Void> unsubscribeFromKeyword(@PathVariable Long keywordId, Authentication authentication) {
        String userId = authentication.getName();
        newsService.unsubscribeFromKeyword(userId, keywordId);
        return ResponseEntity.ok().build();
    }

    //뉴스 수집 스케줄러 트리거(테스트용)
    @GetMapping("/news/collect/trigger")
    public void newsCollectTrigger() throws JsonProcessingException {
        newsCollectScheduler.collectNews();
    }
}
