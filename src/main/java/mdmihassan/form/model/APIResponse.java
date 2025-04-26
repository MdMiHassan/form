package mdmihassan.form.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {

    private boolean success;
    private Instant timestamp;
    private int status;
    private String message;
    private T data;
    private ErrorResponse error;

    public static <R> APIResponse<R> ok(R response, String message) {
        return newOk(response, message);
    }

    public static <R> APIResponse<R> ok(R response) {
        return newOk(response, Messages.REQUEST_PROCESSED.toString());
    }

    private static <R> APIResponse<R> newOk(R response, String message) {
        return createApiResponse(true, HttpStatus.OK.value(), response, message, null);
    }

    public static APIResponse<Object> badRequest(ErrorResponse errorResponse) {
        return newBadRequest(errorResponse, Messages.BAD_REQUEST.toString());
    }

    public static APIResponse<Object> badRequest(ErrorResponse errorResponse, String message) {
        return newBadRequest(errorResponse, message);
    }

    private static APIResponse<Object> newBadRequest(ErrorResponse errorResponse, String message) {
        return createApiResponse(false, HttpStatus.BAD_REQUEST.value(), null, message, errorResponse);
    }

    public static APIResponse<Object> unauthorized(ErrorResponse errorResponse) {
        return createApiResponse(false, HttpStatus.UNAUTHORIZED.value(), null,
                Messages.UNAUTHORIZED.toString(), errorResponse);
    }

    public static APIResponse<Object> forbidden(ErrorResponse errorResponse) {
        return createApiResponse(false, HttpStatus.FORBIDDEN.value(), null,
                Messages.FORBIDDEN.toString(), errorResponse);
    }

    public static APIResponse<Object> notFound(ErrorResponse errorResponse) {
        return createApiResponse(false, HttpStatus.NOT_FOUND.value(), null,
                Messages.NOT_FOUND.toString(), errorResponse);
    }

    public static APIResponse<Object> conflict(ErrorResponse errorResponse) {
        return createApiResponse(false, HttpStatus.CONFLICT.value(), null,
                Messages.CONFLICT.toString(), errorResponse);
    }

    public static APIResponse<Object> internalServerError(ErrorResponse errorResponse) {
        return createApiResponse(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), null,
                Messages.INTERNAL_SERVER_ERROR.toString(), errorResponse);
    }

    private static <R> APIResponse<R> createApiResponse(boolean success,
                                                        int code,
                                                        R response,
                                                        String message,
                                                        ErrorResponse error) {
        APIResponse<R> apiResponse = new APIResponse<>();
        apiResponse.setSuccess(success);
        apiResponse.setStatus(code);
        apiResponse.setData(response);
        apiResponse.setMessage(message);
        apiResponse.setTimestamp(Instant.now());
        apiResponse.setError(error);
        return apiResponse;
    }

    public enum Messages {

        REQUEST_PROCESSED("Request processed successfully"),
        DATA_RETRIEVED("Data retrieved successfully"),
        RESOURCE_CREATED("Resource created successfully"),
        RESOURCE_UPDATED("Resource updated successfully"),
        RESOURCE_DELETED("Resource deleted successfully"),
        OPERATION_COMPLETED("Operation completed successfully"),
        REQUEST_ACCEPTED("Request accepted for processing"),
        AUTH_SUCCESS("Authentication successful"),
        INVALID_REQUEST("Invalid request"),
        BAD_REQUEST("Bad request"),
        UNAUTHORIZED("Unauthorized access"),
        FORBIDDEN("Access denied"),
        NOT_FOUND("Resource not found"),
        CONFLICT("Conflict occurred"),
        INTERNAL_SERVER_ERROR("We are facing some problems now, we are working hard to get it fixed!"),
        INVALID_INPUT("Invalid input provided"),
        MISSING_FIELD("A required field is missing"),
        INVALID_CREDENTIALS("Invalid username or password"),
        TOKEN_EXPIRED("Authentication token has expired"),
        ACCESS_DENIED("Access is denied"),
        RESOURCE_NOT_FOUND("Requested resource was not found"),
        DUPLICATE_RESOURCE("Resource already exists"),
        INSUFFICIENT_BALANCE("Insufficient balance to complete operation"),
        SERVICE_UNAVAILABLE("Dependent service is unavailable"),
        TOO_MANY_REQUESTS("Rate limit exceeded"),
        UNSUPPORTED_MEDIA_TYPE("Unsupported media type"),
        INVALID_STATE("Operation not allowed in the current state"),
        DEPENDENCY_FAILURE("A required external dependency failed");

        private final String message;

        Messages(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {

        private ErrorCode code;
        private String message;
        private Object details;

        public enum ErrorCode {
            REQUEST_VALIDATION_FAILED,
            AUTHENTICATION_FAILED,
            AUTHORIZATION_FAILED,
            BUSINESS_ERROR,
            NOT_FOUND,
            CONFLICT_ERROR,
            INTERNAL_SERVER_ERROR,
            THIRD_PARTY_ERROR,
            RATE_LIMIT_ERROR,
            INVALID_INPUT,
            UNSUPPORTED_OPERATION
        }

        @Data
        public static class GenericDetails {
            private boolean success;
            private String message;
        }

    }

}
