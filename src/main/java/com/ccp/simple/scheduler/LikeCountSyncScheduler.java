package com.ccp.simple.scheduler;

import com.ccp.simple.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeCountSyncScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final NewsService newsService;

    // 10분마다 실행 (cron = "0 */10 * * * *")
    @Scheduled(cron = "0 */10 * * * *")
    public void syncLikeCountsToDb() {
        log.info("Redis '좋아요' 수 DB 동기화 시작");
        Set<String> likeKeys = redisTemplate.keys("news:like:*");

        if (likeKeys == null || likeKeys.isEmpty()) {
            log.info("동기화할 '좋아요' 데이터가 없습니다.");
            return;
        }

        for (String key : likeKeys) {
            try {
                Long newsId = Long.parseLong(key.split(":")[2]);
                Long likeCount = redisTemplate.opsForSet().size(key);

                if (likeCount != null) {
                    newsService.updateLikeCount(newsId, likeCount.intValue());
                }
            } catch (Exception e) {
                log.error("키 '{}' 처리 중 오류 발생: {}", key, e.getMessage());
            }
        }
        log.info("Redis '좋아요' 수 DB 동기화 완료");
    }
}
