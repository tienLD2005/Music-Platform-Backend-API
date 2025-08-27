package com.ra.base_spring_boot.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.repository.IBlacklistedTokenRepository;
import com.ra.base_spring_boot.security.principle.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final MyUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final IBlacklistedTokenRepository blacklistedTokenRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (blacklistedTokenRepository.existsByToken(token)) {
                writeErrorResponse(response, "Token has been revoked", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!jwtProvider.validateToken(token)) {
                writeErrorResponse(response, "Invalid or expired token", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String email = jwtProvider.extractEmail(token);
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(email);
            } catch (UsernameNotFoundException ex) {
                writeErrorResponse(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!jwtProvider.validateToken(token, userDetails)) {
                writeErrorResponse(response, "Token does not match the user", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Cannot authenticate JWT: ", e);
            writeErrorResponse(response, "Unauthorized", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) return null;
        header = header.trim();
        if (header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return null;
    }

    private void writeErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", status);
        error.put("error", HttpStatus.valueOf(status).getReasonPhrase());
        error.put("message", message);

        MAPPER.writeValue(response.getOutputStream(), error);
    }
}
