package xjanua.backend.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import xjanua.backend.util.constant.JwtConstants;

import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${jwt.base64-secret}")
    private String jwtKey;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    private SecretKey getSecretKey() {
        byte[] keyBytes = jwtKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, JwtConstants.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(JwtConstants.JWT_ALGORITHM)
                .build();

        jwtDecoder.setJwtValidator(jwt -> {
            String type = jwt.getClaimAsString("token_type");
            if (!"access".equals(type)) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Refresh token cannot access APIs", null));
            }
            return OAuth2TokenValidatorResult.success();
        });

        return jwtDecoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }
}
