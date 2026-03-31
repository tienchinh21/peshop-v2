package xjanua.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import xjanua.backend.dto.RestResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // Tùy chỉnh thông điệp ở đây
        String customMessage = "Token is invalid or expired";

        RestResponse<Object> res = RestResponse.error(customMessage, "UNAUTHORIZED");
        mapper.writeValue(response.getWriter(), res);
    }
}