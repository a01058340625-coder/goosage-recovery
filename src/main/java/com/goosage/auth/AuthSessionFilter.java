package com.goosage.auth;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthSessionFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // ✅ 컨텍스트 패스 제거한 "순수 경로"로 판정 (중요)
        String uri = request.getRequestURI();         // 예: /goosage-api/auth/login 또는 /auth/login
        String ctx = request.getContextPath();        // 예: /goosage-api (없으면 "")
        String path = uri.substring(ctx.length());    // 예: /auth/login

        String method = request.getMethod();

        // 0) health는 무조건 통과(서버 상태 확인용)
        if (path.startsWith("/health")) return true;

        // 1) auth는 항상 통과 (/auth, /auth/login 다 포함)
        if (path.startsWith("/auth")) return true;

        // 2) GET은 전부 공개
        if ("GET".equalsIgnoreCase(method)) return true;

        // 3) 그 외(POST/PUT/PATCH/DELETE)는 보호
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Long userId = (session == null) ? null : (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);

        if (userId == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.getWriter().write("{\"success\":false,\"message\":\"UNAUTHORIZED\",\"data\":null}");
            return;
        }

        chain.doFilter(req, res);
    }
}
