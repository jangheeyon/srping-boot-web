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
public class ViewCountSyncScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final NewsService newsService;

    // 5분마다 실행
    @Scheduled(cron = "0 */5 * * * *")
    public void syncViewCountsToDb() {
        log.info("Redis '조회수' DB 동기화 시작");
        Set<String> viewKeys = redisTemplate.keys("news:view:*");

        if (viewKeys == null || viewKeys.isEmpty()) {
            log.info("동기화할 '조회수' 데이터가 없습니다.");
            return;
        }

        for (String key : viewKeys) {
            try {
                Long newsId = Long.parseLong(key.split(":")[2]);
                String viewCountStr = redisTemplate.opsForValue().get(key);

                if (viewCountStr != null) {
                    int viewCount = Integer.parseInt(viewCountStr);
                    newsService.updateViewCount(newsId, viewCount);
                }
            } catch (Exception e) {
                log.error("키 '{}' 처리 중 오류 발생: {}", key, e.getMessage());
            }
        }
        log.info("Redis '조회수' DB 동기화 완료");
    }
}
