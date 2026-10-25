package mz.co.bankchallenge.banking_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.bankchallenge.banking_api.exception.ApiError;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

    @Autowired
    private JwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, ObjectMapper objectMapper) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)-> authorize
                        .anyRequest().authenticated())
                .oauth2ResourceServer(
                        (oauth2)-> oauth2.jwt(
                                jwt-> jwt.jwtAuthenticationConverter(jwtConverter)
                        ))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                writeSecurityError(objectMapper, request, response,
                                        401, "Unauthorized", "Authentication is required or invalid"))
                        .accessDeniedHandler((request, response, exception) ->
                                writeSecurityError(objectMapper, request, response,
                                        403, "Forbidden", "You do not have permission")))
                .sessionManagement(
                        session-> session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    private void writeSecurityError(
            ObjectMapper objectMapper,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            int status,
            String error,
            String message) throws java.io.IOException {
        String correlationId = UUID.randomUUID().toString();
        response.setStatus(status);
        response.setContentType("application/json");
        response.setHeader("X-Correlation-Id", correlationId);
        objectMapper.writeValue(response.getOutputStream(), new ApiError(
                Instant.now(), status, error, message,
                request.getRequestURI(), correlationId));
    }
}
