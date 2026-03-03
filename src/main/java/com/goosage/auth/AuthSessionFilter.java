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
	    String path = uri.substring(ctx.length());

	    // ✅ 내부 서버-서버 호출 (세션 없이 허용)
	    if (path.startsWith("/internal")) return true;

	    // 공개 엔드포인트
	    if (path.equals("/") || path.startsWith("/health") || path.startsWith("/hello")) return true;

	    // 로그인(너 실제 매핑)
	    if (path.equals("/login")) return true;

	    // (선택) auth 계열
	 // 공개 엔드포인트
	    if (path.equals("/") || path.startsWith("/health") || path.startsWith("/hello")) return true;
	    if (path.startsWith("/internal/")) return true;
	    
	    // academy health/debug (개발 중)
	    if (path.equals("/academy/health")) return true;
	    if (path.startsWith("/academy/debug")) return true;

	    // preflight
	    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

	    return false;
	}



    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Long userId = (session == null) ? null : (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);

        if (userId == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"success\":false,\"message\":\"UNAUTHORIZED\",\"data\":null}");
            return;
        }

        chain.doFilter(req, res);
    }
}
