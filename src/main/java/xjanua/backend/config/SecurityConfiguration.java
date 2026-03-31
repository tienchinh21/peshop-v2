package xjanua.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import xjanua.backend.util.PublicEndpoints;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

        private final JwtAuthenticationConverter jwtAuthenticationConverter;
        private final CorsConfig corsConfig;
        private final CustomAccessDeniedHandler accessDeniedHandler;
        private final CustomAuthenticationEntryPoint authenticationEntryPoint;

        public SecurityConfiguration(JwtAuthenticationConverter jwtAuthenticationConverter, CorsConfig corsConfig,
                        CustomAccessDeniedHandler accessDeniedHandler,
                        CustomAuthenticationEntryPoint authenticationEntryPoint) {
                this.jwtAuthenticationConverter = jwtAuthenticationConverter;
                this.corsConfig = corsConfig;
                this.accessDeniedHandler = accessDeniedHandler;
                this.authenticationEntryPoint = authenticationEntryPoint;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                                .csrf(c -> c.disable())
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers(PublicEndpoints.PUBLIC_URLS)
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer((oauth2) -> oauth2
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))

                                .formLogin(f -> f.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }
}