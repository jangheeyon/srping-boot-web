package com.ccp.simple.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class ActivityLogAspect {

    @Around("@annotation(logActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint, LogActivity logActivity) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();
        String method = request.getMethod();
        String url = request.getRequestURI();
        String activity = logActivity.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : "GUEST";

        log.info("[Activity Log] User: {}, IP: {}, Method: {}, URL: {}, Action: {}", userId, ip, method, url, activity);

        try {
            Object result = joinPoint.proceed();
            log.info("[Activity Log] Completed: {} - Success", activity);
            return result;
        } catch (Throwable throwable) {
            log.error("[Activity Log] Failed: {} - Error: {}", activity, throwable.getMessage());
            throw throwable;
        }
    }
}
