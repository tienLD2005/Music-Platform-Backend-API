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
        description = "Nhập token JWT bắt đầu với 'Bearer '"
)
public class SecurityConfig
{
    private final MyUserDetailsService userDetailsService;
    private final JwtEntryPoint jwtEntryPoint;
    private final AccessDenied accessDenied;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
                .cors(cf -> cf.configurationSource(request ->
                {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:8080"));
                    config.setAllowedMethods(List.of("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(List.of("*"));
                    config.setExposedHeaders(List.of("*"));
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        url -> url

                                // API Banner
                                .requestMatchers("GET", "/api/v1/banner/**").permitAll()
                                .requestMatchers("POST", "/api/v1/banner/**").hasAuthority(RoleName.ROLE_ADMIN.toString())
                                .requestMatchers("DELETE", "/api/v1/banner/**").hasAuthority(RoleName.ROLE_ADMIN.toString())

                                .requestMatchers("/api/v1/admin/**").hasAuthority(RoleName.ROLE_ADMIN.toString())
                                .requestMatchers("/api/v1/user/**").hasAuthority(RoleName.ROLE_USER.toString())
                                // album and song
                                .requestMatchers(HttpMethod.GET,"/api/v1/albums/*/songs").hasAnyAuthority(RoleName.ROLE_ADMIN.toString(), RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())
                                .requestMatchers(HttpMethod.POST,"/api/v1/albums/*/songs").hasAuthority(RoleName.ROLE_ARTIST.toString())
                                .requestMatchers(HttpMethod.DELETE,"/api/v1/albums/*/songs/**").hasAuthority(RoleName.ROLE_ARTIST.toString())
                                //Album : allow guests to view
                                .requestMatchers("/api/v1/albums").permitAll()
                                .requestMatchers("/api/v1/albums/**").permitAll()


//                                .requestMatchers("/api/v1/artist/**").hasAuthority(RoleName.ROLE_ARTIST.toString())
                                .requestMatchers("api/v1/artists/**").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ADMIN.toString(), RoleName.ROLE_ARTIST.toString())
                                .requestMatchers("api/v1/genres/**").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ADMIN.toString(), RoleName.ROLE_ARTIST.toString())

                                .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exception -> exception
                                .authenticationEntryPoint(jwtEntryPoint)
                                .accessDeniedHandler(accessDenied)
                )
                .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception
    {
        return auth.getAuthenticationManager();
    }
}
