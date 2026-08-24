package com.genesys.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if(path.startsWith("/api/auth/login") || path.startsWith("/actuator/health")|| path.equals("/index.html")
                || path.equals("/")  || path.endsWith(".html")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = extractTokenFromCookies(request);
        if(token ==null || !jwtService.isValid(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request,response);
    }


    private String extractTokenFromCookies(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;

        for(Cookie cookie: cookies){
            if("token".equals(cookie.getName())) return cookie.getValue();
        }

        return null;
    }
}
