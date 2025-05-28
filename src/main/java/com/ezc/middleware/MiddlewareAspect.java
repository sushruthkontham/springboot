package com.ezc.middleware;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MiddlewareAspect {

    private final MiddlewareRegistry registry;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @Before("@annotation(com.ezc.middleware.UseMiddleware)")
    public void handle(JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        UseMiddleware annotation = method.getAnnotation(UseMiddleware.class);

        String[] raw = annotation.names();
        for (String entry : raw) {
            String[] parts = entry.split(":", 2);
            String name = parts[0].trim();
            String param = parts.length > 1 ? parts[1] : null;

            Middleware middleware = registry.get(name);
            if (middleware == null) {
                throw new RuntimeException("Middleware not found:" + name);
            }
            middleware.handle(request, response, param);
        }
    }
}
