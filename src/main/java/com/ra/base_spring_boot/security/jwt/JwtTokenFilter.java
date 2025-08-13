package com.ra.base_spring_boot.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra.base_spring_boot.repository.IBlacklistedTokenRepository;
import com.ra.base_spring_boot.security.principle.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final MyUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final IBlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromUser(request);
            if (token != null) {

                if (blacklistedTokenRepository.existsByToken(token)) {
                    throw new RuntimeException("Token đã bị thu hồi");
                }

                if (jwtProvider.validateToken(token)) {
                    String email = jwtProvider.extractEmail(token);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (!jwtProvider.validateToken(token, userDetails)) {
                        throw new RuntimeException("Token không khớp với người dùng");
                    }

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } else {
                    throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn");
                }
            }
        } catch (Exception e) {
            log.error("Không thể xác thực JWT: {}", e.getMessage());
            writeErrorResponse(response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromUser(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Unauthorized");
        error.put("message", message);

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
