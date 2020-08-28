package com.tpssoft.hham.security;

import com.tpssoft.hham.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ApiTokenAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private SessionService sessionService;

    private String extractToken(String authorizationHeader) {
        final String TOKEN_HEADER_START = "Bearer ";
        if (authorizationHeader == null ||
                authorizationHeader.isBlank() ||
                !authorizationHeader.startsWith(TOKEN_HEADER_START)) {
            return "";
        }
        return authorizationHeader.substring(TOKEN_HEADER_START.length());
    }

    private HhamUserDetails createUserDetails(String token) {
        var user = sessionService.findByToken(token);
        return new HhamUserDetails(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getHashedPassword(),
                token,
                List.of(new SimpleGrantedAuthority(user.isAdmin() ? "SYSADMIN" : "USER"))
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        // Skip login, image requests, and account creation
        var pattern = "/auth/login|/activate|/images/[0-9a-fA-F]+|/invitation/[0-9a-fA-F]+";
        if (request.getRequestURI().matches(pattern)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = extractToken(request.getHeader("Authorization"));
        if (!sessionService.isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        sessionService.refreshToken(token);
        HhamUserDetails userDetails = createUserDetails(token);
        var authToken = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
