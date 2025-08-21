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
        description = "Nhập token JWT"
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
                                .requestMatchers("POST", "/api/v1/banner/**").hasAuthority(RoleName.ROLE_ADMIN.toString())
                                .requestMatchers("DELETE", "/api/v1/banner/**").hasAuthority(RoleName.ROLE_ADMIN.toString())

                                .requestMatchers("/api/v1/admin/**").hasAuthority(RoleName.ROLE_ADMIN.toString())

                                // API Song (Artist)
                                .requestMatchers(HttpMethod.GET,"/api/v1/albums/*/songs").hasAnyAuthority(RoleName.ROLE_ADMIN.toString(), RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())
                                .requestMatchers(HttpMethod.POST,"/api/v1/albums/*/songs").hasAuthority(RoleName.ROLE_ARTIST.toString())
                                .requestMatchers(HttpMethod.DELETE,"/api/v1/albums/*/songs/**").hasAuthority(RoleName.ROLE_ARTIST.toString())

                                //API artist album
                                .requestMatchers("api/v1/artist/albums/**").hasAuthority(RoleName.ROLE_ARTIST.toString())

                                //API Comment artist
                                .requestMatchers("api/v1/artist/comments/**").hasAuthority(RoleName.ROLE_ARTIST.toString())

                                //API Comment User
                                .requestMatchers("api/v1/client/comments/**").hasAnyAuthority(RoleName.ROLE_ARTIST.toString(), RoleName.ROLE_USER.toString())

                                //API Lyrics Artist
                                .requestMatchers("api/v1/artist/lyrics/**").hasAuthority(RoleName.ROLE_ARTIST.toString())

                                //API Wishlist
                                .requestMatchers("/api/v1/wishlists/**").hasAuthority(RoleName.ROLE_USER.toString())

                                //API Subscription Plan (Admin)
                                .requestMatchers("/api/v1/admin/subscription_plan/**").hasAuthority(RoleName.ROLE_ADMIN.toString())

                                .requestMatchers("api/v1/subscriptions/**").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())
                                .requestMatchers("api/v1/payments/**").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())

                                .requestMatchers(HttpMethod.POST, "/api/v1/comments/*/reactions").hasAuthority(RoleName.ROLE_USER.toString())
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/comments/*/reactions").hasAuthority(RoleName.ROLE_USER.toString())

                                .requestMatchers(HttpMethod.POST, "/api/v1/comment-reactions/*").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/comment-reactions/*").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())

                                .requestMatchers("/api/v1/admin/statistics/**").hasAuthority(RoleName.ROLE_ADMIN.toString())

                                .requestMatchers(HttpMethod.POST, "/api/v1/follows/artists/*")
                                .hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())

                                .requestMatchers(HttpMethod.DELETE, "/api/v1/follows/artists/*")
                                .hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())

                                .requestMatchers(HttpMethod.GET, "/api/v1/follows/me/artists")
                                .hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())

                                .requestMatchers(HttpMethod.GET, "/api/v1/follows/artists/*/followers")
                                .hasAnyAuthority(RoleName.ROLE_ADMIN.toString(), RoleName.ROLE_ARTIST.toString())

                                .requestMatchers(HttpMethod.GET, "/api/v1/follows/artists/*/followers/count")
                                .hasAnyAuthority(RoleName.ROLE_ADMIN.toString(), RoleName.ROLE_ARTIST.toString())

                                // Song history
                                .requestMatchers("/api/v1/song-history/**").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())

                                // Song reactions
                                .requestMatchers("/api/v1/song-reactions/**").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())

                                // download songs
                                .requestMatchers("/api/v1/download-song/**").hasAuthority(RoleName.ROLE_USER.toString())

                                // profile
                                .requestMatchers("/api/v1/profile/**").hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())


                                // Genres
                                .requestMatchers(HttpMethod.GET, "/api/v1/genres/trending").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/genres/**")
                                .hasAnyAuthority(RoleName.ROLE_ADMIN.toString(), RoleName.ROLE_USER.toString(), RoleName.ROLE_ARTIST.toString())
                                .requestMatchers(HttpMethod.POST, "/api/v1/genres/**").hasAuthority(RoleName.ROLE_ADMIN.toString())
                                .requestMatchers(HttpMethod.PUT, "/api/v1/genres/**").hasAuthority(RoleName.ROLE_ADMIN.toString())
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/genres/**").hasAuthority(RoleName.ROLE_ADMIN.toString())


                                // Playlists
                                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/playlists/**")
                                .hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ADMIN.toString())
                                .requestMatchers(HttpMethod.POST, "/api/v1/users/*/playlists/**")
                                .hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ADMIN.toString())
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*/playlists/**")
                                .hasAnyAuthority(RoleName.ROLE_USER.toString(), RoleName.ROLE_ADMIN.toString())


                                // Song
                                .requestMatchers(HttpMethod.GET, "/api/v1/songs/**").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/songs/**").hasAuthority(RoleName.ROLE_ADMIN.toString())

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
