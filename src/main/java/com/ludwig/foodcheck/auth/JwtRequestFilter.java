package com.ludwig.foodcheck.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.userdetails.User;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String jwt = getJwtFromCookies(request);

        if (jwt != null && jwtUtil.validateToken(jwt)) {
            Long userId = jwtUtil.extractUserId(jwt);
            String role = jwtUtil.extractRole(jwt);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                User userDetails = new User(userId.toString(), "", authorities);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        chain.doFilter(request, response);
    }

    // Helper method to extract JWT from cookies
    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .findFirst();

            return jwtCookie.map(Cookie::getValue).orElse(null);
        }
        return null;
    }

}
