package com.goosage.auth;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthSessionFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        String path = uri.substring(ctx.length());

        if (path.startsWith("/auth/")) return true;
        if (path.startsWith("/internal/")) return true;
        if (path.equals("/") || path.startsWith("/health") || path.startsWith("/hello")) return true;
        if (path.equals("/academy/health")) return true;
        if (path.startsWith("/academy/debug")) return true;
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Long userId = (session == null) ? null : (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);

        if (userId == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"success\":false,\"message\":\"UNAUTHORIZED\",\"data\":null}");
            return;
        }

        chain.doFilter(req, res);
    }
}