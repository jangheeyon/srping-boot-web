package com.ccp.simple.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    SseEmitter subscribe(String userId);

    void send(String userId, Object data);
}
