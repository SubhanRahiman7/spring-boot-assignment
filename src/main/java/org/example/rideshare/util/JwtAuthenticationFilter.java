package org.example.rideshare.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        TokenInfo tokenInfo = extractTokenInfo(authHeader);

        if (tokenInfo != null && shouldAuthenticate()) {
            authenticateRequest(request, tokenInfo);
        }

        chain.doFilter(request, response);
    }

    private TokenInfo extractTokenInfo(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        String jwtToken = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            String username = jwtUtil.extractUsername(jwtToken);
            String role = jwtUtil.extractRole(jwtToken);
            return new TokenInfo(jwtToken, username, role);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean shouldAuthenticate() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void authenticateRequest(HttpServletRequest request, TokenInfo tokenInfo) {
        if (jwtUtil.validateToken(tokenInfo.token, tokenInfo.username)) {
            UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(
                    tokenInfo.username, tokenInfo.role, request);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(
            String username, String role, HttpServletRequest request) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authToken;
    }

    private static class TokenInfo {
        final String token;
        final String username;
        final String role;

        TokenInfo(String token, String username, String role) {
            this.token = token;
            this.username = username;
            this.role = role;
        }
    }
}

