package mz.co.bankchallenge.banking_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
/** Maps application, validation, security and persistence failures to API errors. */
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(
            ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return response(status, exception.getReason(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleMethodValidation(
            HandlerMethodValidationException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Request parameters are invalid", request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request) {
        String message = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiError> handleInvalidParameter(
            Exception exception, HttpServletRequest request) {
        String message = exception instanceof MethodArgumentTypeMismatchException mismatch
                ? "Invalid value for parameter '" + mismatch.getName() + "'"
                : "Required parameter is missing";
        return response(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST,
                "Request body is malformed or contains an invalid value", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        log.warn("Data integrity violation on {}", request.getRequestURI());
        return response(HttpStatus.CONFLICT,
                "The request conflicts with existing data or a database constraint", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException exception, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, "Authentication is required or invalid", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.error("Unexpected error. correlationId={} path={}",
                correlationId, request.getRequestURI(), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", request, correlationId);
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status, String message, HttpServletRequest request) {
        return response(status, message, request, UUID.randomUUID().toString());
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status, String message, HttpServletRequest request, String correlationId) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message == null ? status.getReasonPhrase() : message,
                request.getRequestURI(),
                correlationId
        );
        return ResponseEntity.status(status)
                .header("X-Correlation-Id", correlationId)
                .body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException exception, HttpServletRequest request) {
        return response(HttpStatus.FORBIDDEN, "You do not have permission", request);
    }
}
