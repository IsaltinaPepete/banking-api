
package mz.co.bankchallenge.banking_api.config;

import tools.jackson.databind.json.JsonMapper;
import mz.co.bankchallenge.banking_api.exception.ApiError;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
/** Configures stateless JWT authentication and method-level authorization. */
public class SecurityConfig {

    private final JwtConverter jwtConverter;

    public SecurityConfig(JwtConverter jwtConverter) {
        this.jwtConverter = jwtConverter;
    }

    @Bean
    /**
     * Builds the security filter chain used by the API.
     *
     * <p>JWT failures and authorization failures are serialized as the API's
     * standard error payload, including a correlation identifier.</p>
     *
     * @param http Spring Security HTTP configuration
     * @param jsonMapper mapper used to serialize security error responses
     * @return configured stateless security filter chain
     * @throws Exception if Spring Security cannot build the filter chain
     */
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JsonMapper jsonMapper) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                .authenticationEntryPoint((request, response, ex) ->
                    writeSecurityError(
                        jsonMapper, request, response,
                        401, "Unauthorized",
                        "Authentication is required or invalid"
                    ))
                .accessDeniedHandler((request, response, ex) ->
                    writeSecurityError(
                        jsonMapper, request, response,
                        403, "Forbidden",
                        "You do not have permission"
                    ))
            )

            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, ex) ->
                    writeSecurityError(
                        jsonMapper, request, response,
                        401, "Unauthorized",
                        "Authentication is required or invalid"
                    ))
                .accessDeniedHandler((request, response, ex) ->
                    writeSecurityError(
                        jsonMapper, request, response,
                        403, "Forbidden",
                        "You do not have permission"
                    ))
            )

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    private void writeSecurityError(
            JsonMapper jsonMapper,
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String error,
            String message) throws IOException {

        String correlationId = UUID.randomUUID().toString();

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Correlation-Id", correlationId);

        ApiError apiError = new ApiError(
            Instant.now(),
            status,
            error,
            message,
            request.getRequestURI(),
            correlationId
        );

        response.getOutputStream().write(
            jsonMapper.writeValueAsBytes(apiError)
        );
    }
}
