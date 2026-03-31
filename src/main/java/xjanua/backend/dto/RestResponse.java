package xjanua.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {
    private Error error;
    private T content;

    public static <T> RestResponse<T> success(T content) {
        return new RestResponse<>(null, content);
    }

    public static <T> RestResponse<T> error(String message, String exception) {
        return new RestResponse<>(new Error(message, exception), null);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Error {
        private String message;
        private String exception;
    }
}