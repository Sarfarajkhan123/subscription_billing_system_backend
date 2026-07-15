package com.subscript.subscription.main.config;

import com.subscript.subscription.service.service.security.CustomUserDetailsService;
import com.subscript.subscription.service.service.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.Customizer;
@Configuration
@EnableMethodSecurity // enables @PreAuthorize on controllers
@RequiredArgsConstructor
public class SecurityBeansConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Unauthorized - JWT token is missing or invalid\"}");
                        }))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login")
                        .permitAll()

                        // Error dispatch must be reachable so real HTTP status codes
                        // (400/403/500) surface instead of being masked as 401 by the
                        // authenticationEntryPoint on the anonymous /error forward.
                        .requestMatchers("/error")
                        .permitAll()

                        // IT Admin
                        .requestMatchers("/api/admin/**")
                        .hasRole("IT_ADMIN")

                        // Finance
                        .requestMatchers("/api/payments/**")
                        .hasRole("FINANCE")

                        // Customers may read only their OWN invoices (ownership
                        // enforced in the controller): the customer list and the
                        // single-invoice detail. Everything else (list-all, by-status,
                        // mark-paid/overdue, generate, delete) is Finance / IT Admin.
                        .requestMatchers(HttpMethod.GET,
                                "/api/invoices/customer/**", "/api/invoices/*")
                        .hasAnyRole("CUSTOMER", "FINANCE", "IT_ADMIN")

                        .requestMatchers("/api/invoices/**")
                        .hasAnyRole("FINANCE", "IT_ADMIN")

                        // Dashboard overview metrics — Finance / IT Admin only.
                        .requestMatchers("/api/dashboard/**")
                        .hasAnyRole("FINANCE", "IT_ADMIN")

                        // Product + IT Admin manage the catalog; any authenticated
                        // user (incl. customers) may VIEW services & plans.
                        .requestMatchers(HttpMethod.GET, "/api/services/**", "/api/plans/**")
                        .authenticated()

                        .requestMatchers("/api/services/**", "/api/plans/**")
                        .hasAnyRole("PRODUCT", "IT_ADMIN")

                        // Support
                        .requestMatchers("/api/support/**")
                        .hasRole("SUPPORT")

                        // All remaining endpoints
                        .anyRequest().authenticated())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(List.of("http://localhost:4200"));
    configuration.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS"));

    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);

    return source;
}
}