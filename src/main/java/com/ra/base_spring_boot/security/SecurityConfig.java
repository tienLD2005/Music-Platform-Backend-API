package com.ra.base_spring_boot.security;

import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.security.exception.AccessDenied;
import com.ra.base_spring_boot.security.exception.JwtEntryPoint;
import com.ra.base_spring_boot.security.jwt.JwtTokenFilter;
import com.ra.base_spring_boot.security.principle.MyUserDetailsService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@OpenAPIDefinition(
        info = @Info(title = "Music API", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Nhập token JWT"
)
public class SecurityConfig {

    private final MyUserDetailsService userDetailsService;
    private final JwtEntryPoint jwtEntryPoint;
    private final AccessDenied accessDenied;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // --- Banner ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/banner/**").hasAuthority(RoleName.ROLE_ADMIN.toString())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/banner/**").hasAuthority(RoleName.ROLE_ADMIN.toString())

                        // --- Admin APIs ---
                        .requestMatchers("/api/v1/admin/**").hasAuthority(RoleName.ROLE_ADMIN.toString())

                        // --- Album & Songs (Artist) ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/artist/albums/*/songs")
                        .hasAuthority(RoleName.ROLE_ARTIST.toString())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/artist/albums/*/songs/*")
                        .hasAuthority(RoleName.ROLE_ARTIST.toString())

                        .requestMatchers("api/v1/artist/albums/**").hasAuthority(RoleName.ROLE_ARTIST.toString())
                        .requestMatchers("api/v1/artist/comments/**").hasAuthority(RoleName.ROLE_ARTIST.toString())
                        .requestMatchers("api/v1/artist/lyrics/**").hasAuthority(RoleName.ROLE_ARTIST.toString())

                        // --- Comments ---
                        .requestMatchers("/api/v1/client/comments/**").hasAnyAuthority(authorities(RoleName.ROLE_ARTIST, RoleName.ROLE_USER))
                        .requestMatchers(HttpMethod.GET, "/api/v1/comment-reactions/*").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.POST, "/api/v1/comment-reactions/*").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/comment-reactions/*").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))

                        // --- Wishlist ---
                        .requestMatchers("/api/v1/wishlists/**").hasAnyAuthority(authorities(RoleName.ROLE_ARTIST, RoleName.ROLE_USER))

                        // --- Subscriptions & Payments ---
                        .requestMatchers("api/v1/subscriptions/**").hasAnyAuthority(authorities(RoleName.ROLE_ARTIST, RoleName.ROLE_USER))
                        .requestMatchers("api/v1/payments/**").hasAnyAuthority(authorities(RoleName.ROLE_ARTIST, RoleName.ROLE_USER))

                        // --- Follows ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/follows/artists/*").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/follows/artists/*").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.GET, "/api/v1/follows/me/artists").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.GET, "/api/v1/follows/artists/*/followers").hasAnyAuthority(authorities(RoleName.ROLE_ADMIN, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.GET, "/api/v1/follows/artists/*/followers/count").hasAnyAuthority(authorities(RoleName.ROLE_ADMIN, RoleName.ROLE_ARTIST))

                        // --- Song history & reactions ---
                        .requestMatchers("/api/v1/song-history/**").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers("/api/v1/song-reactions/**").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))

                        // --- Download songs ---
                        .requestMatchers("/api/v1/download-song/**").hasAuthority(RoleName.ROLE_USER.toString())

                        // --- Profile ---
                        .requestMatchers("/api/v1/profile/**").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))

                        // --- Playlists ---
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*/playlists/**").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/playlists").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/playlists/*/songs").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*/playlists/*/songs").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*/playlists/*").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*/playlists/*/songs/*").hasAnyAuthority(authorities(RoleName.ROLE_USER, RoleName.ROLE_ARTIST))

                        // --- Default ---
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtEntryPoint)
                        .accessDeniedHandler(accessDenied)
                )
                .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    private String[] authorities(RoleName... roles) {
        return Arrays.stream(roles).map(Enum::toString).toArray(String[]::new);
    }
}
