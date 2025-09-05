package com.ccp.simple.service.impl;

import com.ccp.simple.dto.NewsResponseDto;
import com.ccp.simple.mapper.NewsMapper;
import com.ccp.simple.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final NewsMapper newsMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public List<NewsResponseDto> recommendNewsBySimilarUsers(String userId, int limit) {
        // 현재 사용자와 가장 유사한 취향의 사용자들이 좋아한 뉴스 추천
        // 1. 현재 사용자와 좋아요한 뉴스가 같은 사용자 조회
        List<String> similarUserIds = newsMapper.searchSimilarUsers(userId, 5); //상위 5명 조회

        if (similarUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 유사 사용자들이 좋아한 모든 뉴스 ID 조회
        List<Long> recommendedNewsIds = newsMapper.searchNewsIdsBySimilarUsers(similarUserIds);

        if (recommendedNewsIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 현재 사용자가 이미 좋아한 뉴스 ID 조회
        List<Long> likedNewsIdsByUser = newsMapper.searchLikedNewsIdsByUser(userId);

        // 4. 추천 목록에서 현재 사용자가 이미 좋아한 뉴스 제외
        List<Long> distinctRecommendNewsIds = new ArrayList<>();
        Set<Long> set = new HashSet<>(); // 중복 제거

        for (Long newsId : recommendedNewsIds) {
            if (!likedNewsIdsByUser.contains(newsId) && set.add(newsId)) {
                distinctRecommendNewsIds.add(newsId);
                //limit 까지만 조회
                if (distinctRecommendNewsIds.size() >= limit) {
                    break;
                }
            }
        }

        if (distinctRecommendNewsIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 5. 뉴스 전체 정보 조회 + Redis 데이터 결합
        List<NewsResponseDto> recommendations = newsMapper.findNewsByIds(distinctRecommendNewsIds);
        matchEsResultsWithRedisData(recommendations, userId);

        return recommendations;
    }

    //좋아요 수 매칭
    private void matchEsResultsWithRedisData(List<NewsResponseDto> newsList, String userId) {
        for (NewsResponseDto news : newsList) {
            String likeKey = "news:like:" + news.getNewsId();
            Long likeCount = redisTemplate.opsForSet().size(likeKey);
            news.setLikeCount(likeCount != null ? likeCount.intValue() : 0);

            if (userId != null) {
                news.setLiked(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likeKey, userId)));
            } else {
                news.setLiked(false);
            }
        }
    }
}
