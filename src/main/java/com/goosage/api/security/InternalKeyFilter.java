package com.goosage.api.security;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InternalKeyFilter extends OncePerRequestFilter {

    private final String internalKey;

    public InternalKeyFilter(String internalKey) {
        this.internalKey = internalKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String key = request.getHeader("X-INTERNAL-KEY");
        if (internalKey.equals(key)) {
            // ✅ 내부 호출이면 인증 없이 통과
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}