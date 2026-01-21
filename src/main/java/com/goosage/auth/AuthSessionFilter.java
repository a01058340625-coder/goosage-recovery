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
	    String uri = request.getRequestURI();
	    String ctx = request.getContextPath();
	    String path = uri.substring(ctx.length()); // e.g. /api/auth/login

	    // ✅ 무조건 공개(헬스체크/기본)
	    if (path.equals("/") || path.startsWith("/health") || path.startsWith("/hello")) return true;

	    // ✅ 로그인/회원가입은 반드시 공개
	    if (path.startsWith("/auth") || path.startsWith("/api/auth")) return true;

	    // ✅ OPTIONS(프리플라이트)도 통과 (React 붙일 때 중요)
	    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

	    // ✅ (원하면 유지) GET은 전부 공개
	    if ("GET".equalsIgnoreCase(request.getMethod())) return true;

	    return false; // 나머지는 보호
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
