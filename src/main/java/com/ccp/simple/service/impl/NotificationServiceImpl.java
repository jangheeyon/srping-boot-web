package com.ccp.simple.service.impl;

import com.ccp.simple.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(userId, emitter);

        // Emitter가 완료되거나 타임아웃 되면 emitters 맵에서 삭제
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        // 연결 수립 후, 503 Service Unavailable 방지를 위한 더미 이벤트 전송
        sendToClient(userId, "EventStream Created. [userId=" + userId + "]");

        return emitter;
    }

    @Override
    public void send(String userId, Object data) {
        sendToClient(userId, data);
    }

    private void sendToClient(String userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .name("sse")
                        .data(data));
            } catch (IOException e) {
                emitters.remove(userId);
                log.error("SSE 연결 오류 발생 [userId={}]", userId, e);
            }
        }
    }
}
