package com.portfolio.cms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.cms.common.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    private static final int MAX_AUTH_REQUESTS_PER_MINUTE = 20;
    private static final int MAX_CONTACT_REQUESTS_PER_MINUTE = 10;
    private static final long WINDOW_MS = 60_000L;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        int limit = 0;
        if ("POST".equalsIgnoreCase(method) && path.startsWith("/api/auth/login")) {
            limit = MAX_AUTH_REQUESTS_PER_MINUTE;
        } else if ("POST".equalsIgnoreCase(method) && path.startsWith("/api/public/contact")) {
            limit = MAX_CONTACT_REQUESTS_PER_MINUTE;
        }

        if (limit > 0) {
            String clientIp = getClientIp(request);
            String key = clientIp + ":" + path;

            long now = Instant.now().toEpochMilli();
            RequestCounter counter = requestCounts.compute(key, (k, existing) -> {
                if (existing == null || now - existing.windowStart > WINDOW_MS) {
                    return new RequestCounter(now, new AtomicInteger(1));
                }
                existing.count.incrementAndGet();
                return existing;
            });

            if (counter.count.get() > limit) {
                log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, path);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                ApiResponse<Void> apiResponse = ApiResponse.error("Too many requests. Please wait a minute before trying again.");
                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private static class RequestCounter {
        final long windowStart;
        final AtomicInteger count;

        RequestCounter(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
