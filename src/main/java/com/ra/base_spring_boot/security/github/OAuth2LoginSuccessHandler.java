package com.ra.base_spring_boot.security.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String token = jwtProvider.generateToken(oAuth2User.getUsername(),oAuth2User.getUserId(),oAuth2User.getPrimaryRole());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);

        new ObjectMapper().writeValue(response.getWriter(), resp);
    }
}
