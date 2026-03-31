package xjanua.backend.util.exception;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.turkraft.springfilter.parser.InvalidSyntaxException;

import jakarta.persistence.EntityNotFoundException;
import xjanua.backend.dto.RestResponse;

/**
 * Xử lý ngoại lệ toàn cục cho toàn bộ ứng dụng.
 * Bắt các lỗi phát sinh từ controller và trả về phản hồi JSON
 * theo định dạng RestResponse (error + content).
 * 
 * Lưu ý: Không xử lý lỗi xác thực (401) vì đã được
 * CustomAuthenticationEntryPoint đảm nhiệm.
 */
@RestControllerAdvice
public class GlobalException {

        // 400 Bad Request Exceptions
        @ExceptionHandler({
                        IllegalArgumentException.class,
                        HttpMessageNotReadableException.class,
                        InvalidDefinitionException.class,
                        InvalidSyntaxException.class,
                        MaxUploadSizeExceededException.class,
                        ResourceAlreadyExistsException.class,
                        BadRequestException.class,
                        ResourceNotFoundException.class
        })
        public ResponseEntity<RestResponse<Object>> handleBadRequestExceptions(Exception ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(RestResponse.error(ex.getMessage(), ex.getClass().getSimpleName()));
        }

        // 401 Unauthorized Exceptions
        @ExceptionHandler({
                        BadCredentialsException.class,
                        UsernameNotFoundException.class

        })
        public ResponseEntity<RestResponse<Object>> handleUnauthorizedExceptions(Exception ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(RestResponse.error(ex.getMessage(), ex.getClass().getSimpleName()));
        }

        // 403 Forbidden Exceptions
        @ExceptionHandler({
                        AccessDeniedException.class
        })
        public ResponseEntity<RestResponse<Object>> handleForbiddenExceptions(Exception ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(RestResponse.error(ex.getMessage(), ex.getClass().getSimpleName()));
        }

        // 404 Not Found Exceptions
        @ExceptionHandler({
                        EntityNotFoundException.class,
                        NoResourceFoundException.class
        })
        public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(RestResponse.error(ex.getMessage(), ex.getClass().getSimpleName()));
        }

        // 500 Internal Server Error Exceptions
        @ExceptionHandler({
                        JSchException.class,
                        IOException.class,
                        HibernateException.class,
                        SftpException.class,
                        HttpMediaTypeNotSupportedException.class,
        })
        public ResponseEntity<RestResponse<Object>> handleInternalServerErrorExceptions(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(RestResponse.error(ex.getMessage(), ex.getClass().getSimpleName()));
        }

        // Validation Exceptions
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex) {
                BindingResult result = ex.getBindingResult();
                final List<FieldError> fieldErrors = result.getFieldErrors();

                List<String> errors = fieldErrors.stream()
                                .map(f -> f.getDefaultMessage())
                                .collect(Collectors.toList());

                String errorMessage = errors.size() > 1
                                ? String.join(", ", errors)
                                : errors.get(0);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(RestResponse.error(errorMessage, ex.getClass().getSimpleName()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<RestResponse<Object>> handleAllUnhandledExceptions(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(RestResponse.error(ex.getMessage(), ex.getClass().getSimpleName()));
        }

}
