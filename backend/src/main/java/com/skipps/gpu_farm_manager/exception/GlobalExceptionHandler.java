package com.skipps.gpu_farm_manager.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;

record ApiError(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {}

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message) {
        ApiError error = new ApiError(
            status.value(),
            status.getReasonPhrase(),
            message,
            LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(error);
    }

    // ==========================================
    // 404 NOT FOUND Handlers
    // ==========================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("Static resource or endpoint not found: {}", ex.getResourcePath());
        return buildResponse(HttpStatus.NOT_FOUND, "Endpoint not found: " + ex.getResourcePath());
    }

    // ==========================================
    // 409 CONFLICT Handlers
    // ==========================================

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        log.warn("Resource already exists conflict: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidWorkloadStateException.class)
    public ResponseEntity<ApiError> handleInvalidWorkloadStateException(InvalidWorkloadStateException ex) {
        log.warn("Invalid workload state: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Illegal state conflict: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // ==========================================
    // 429 TOO MANY REQUESTS Handlers (Quotas)
    // ==========================================

    @ExceptionHandler(QuotaExceededException.class)
    public ResponseEntity<ApiError> handleQuotaExceededException(QuotaExceededException ex) {
        log.warn("Quota exceeded: {}", ex.getMessage());
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    // ==========================================
    // 422 UNPROCESSABLE CONTENT Handlers (Scheduling / Resources)
    // ==========================================

    @ExceptionHandler(InsufficientResourcesException.class)
    public ResponseEntity<ApiError> handleInsufficientResourcesException(InsufficientResourcesException ex) {
        log.warn("Insufficient resources for workload: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    // ==========================================
    // 503 SERVICE UNAVAILABLE Handlers
    // ==========================================

    @ExceptionHandler(NodeUnavailableException.class)
    public ResponseEntity<ApiError> handleNodeUnavailableException(NodeUnavailableException ex) {
        log.warn("GPU node unavailable: {}", ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(GpuUnavailableException.class)
    public ResponseEntity<ApiError> handleGpuUnavailableException(GpuUnavailableException ex) {
        log.warn("GPU unavailable: {}", ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    // ==========================================
    // 502 BAD GATEWAY Handlers (Inference Engines)
    // ==========================================

    @ExceptionHandler(InferenceServiceException.class)
    public ResponseEntity<ApiError> handleInferenceServiceException(InferenceServiceException ex) {
        log.error("Inference service communication error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_GATEWAY, "Inference service backend error: " + ex.getMessage());
    }

    // ==========================================
    // 400 BAD REQUEST Handlers
    // ==========================================

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> String.format("Field '%s': %s", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining("; "));

        String message = validationErrors.isEmpty()
            ? "Validation failed for request"
            : "Validation failed: " + validationErrors;

        log.warn("Validation failed: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        String violations = ex.getConstraintViolations().stream()
            .map(cv -> String.format("Property '%s': %s", cv.getPropertyPath(), cv.getMessage()))
            .collect(Collectors.joining("; "));

        String message = violations.isEmpty()
            ? "Constraint violation occurred"
            : "Validation constraint violated: " + violations;

        log.warn("Constraint violation: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Malformed HTTP request body: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed or missing JSON request body");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' with value '%s' could not be converted to type '%s'",
            ex.getName(), ex.getValue(), requiredType);

        log.warn("Type mismatch: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    // ==========================================
    // 401 UNAUTHORIZED Handlers
    // ==========================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Authentication failed - bad credentials");
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("Authentication failed - username not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "User not found or credentials invalid");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
    }

    // ==========================================
    // 403 FORBIDDEN Handlers
    // ==========================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied: you do not have permission to access this resource");
    }

    // ==========================================
    // 405 METHOD NOT ALLOWED Handlers
    // ==========================================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod());
        log.warn("Method not allowed: {}", message);
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    // ==========================================
    // 500 INTERNAL SERVER ERROR (Fallback)
    // ==========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {
        log.error("Unhandled internal server error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal server error occurred");
    }
}
