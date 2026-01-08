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
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1) auth는 항상 통과
        if (path.startsWith("/auth")) return true;

        // 2) GET은 전부 공개 (너가 정한 정책)
        if ("GET".equals(method)) return true;

        // 3) 그 외(POST/PUT/PATCH/DELETE)는 보호 시작
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
            res.getWriter().write("""
                {"success":false,"message":"UNAUTHORIZED","data":null}
            """);
            return;
        }

        chain.doFilter(req, res);
    }
}
