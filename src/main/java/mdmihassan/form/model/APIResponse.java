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
    private int status;
    private String message;
    private Instant timestamp;
    private T data;
    private ErrorResponse error;

    public static <R> APIResponse<R> ok(R response, String message) {
        return newOk(response, message);
    }

    public static <R> APIResponse<R> ok(R response) {
        return newOk(response, Messages.REQUEST_PROCESSED.toString());
    }

    private static <R> APIResponse<R> newOk(R response, String message) {
        return createAPIResponse(true, HttpStatus.OK.value(), response, message, null);
    }

    public static APIResponse<Object> badRequest(ErrorResponse errorResponse) {
        return newBadRequest(errorResponse, Messages.BAD_REQUEST.toString());
    }

    public static APIResponse<Object> badRequest(ErrorResponse errorResponse, String message) {
        return newBadRequest(errorResponse, message);
    }

    private static APIResponse<Object> newBadRequest(ErrorResponse errorResponse, String message) {
        return createAPIResponse(false, HttpStatus.BAD_REQUEST.value(), null, message, errorResponse);
    }

    public static APIResponse<Object> unauthorized(ErrorResponse errorResponse) {
        return newUnauthorized(errorResponse, Messages.UNAUTHORIZED.toString());
    }

    public static APIResponse<Object> unauthorized(ErrorResponse errorResponse, String message) {
        return newUnauthorized(errorResponse, message);
    }

    private static APIResponse<Object> newUnauthorized(ErrorResponse errorResponse, String message) {
        return createAPIResponse(false, HttpStatus.UNAUTHORIZED.value(), null, message, errorResponse);
    }

    public static APIResponse<Object> forbidden(ErrorResponse errorResponse) {
        return newForbidden(errorResponse, Messages.FORBIDDEN.toString());
    }

    public static APIResponse<Object> forbidden(ErrorResponse errorResponse, String message) {
        return newForbidden(errorResponse, message);
    }

    public static APIResponse<Object> newForbidden(ErrorResponse errorResponse, String message) {
        return createAPIResponse(false, HttpStatus.FORBIDDEN.value(), null, message, errorResponse);
    }

    public static APIResponse<Object> notFound(ErrorResponse errorResponse) {
        return newNotFound(errorResponse, Messages.NOT_FOUND.toString());
    }

    public static APIResponse<Object> notFound(ErrorResponse errorResponse, String message) {
        return newNotFound(errorResponse, message);
    }

    public static APIResponse<Object> newNotFound(ErrorResponse errorResponse, String message) {
        return createAPIResponse(false, HttpStatus.NOT_FOUND.value(), null, message, errorResponse);
    }

    public static APIResponse<Object> conflict(ErrorResponse errorResponse) {
        return newConflict(errorResponse, Messages.CONFLICT.toString());
    }

    public static APIResponse<Object> conflict(ErrorResponse errorResponse, String message) {
        return newConflict(errorResponse, message);
    }

    public static APIResponse<Object> newConflict(ErrorResponse errorResponse, String message) {
        return createAPIResponse(false, HttpStatus.CONFLICT.value(), null, message, errorResponse);
    }


    public static APIResponse<Object> internalServerError(ErrorResponse errorResponse) {
        return newInternalServerError(errorResponse, Messages.INTERNAL_SERVER_ERROR.toString());
    }

    public static APIResponse<Object> internalServerError(ErrorResponse errorResponse, String message) {
        return newInternalServerError(errorResponse, message);
    }

    public static APIResponse<Object> newInternalServerError(ErrorResponse errorResponse, String message) {
        return createAPIResponse(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), null, message, errorResponse);
    }

    public static APIResponse<Object> notAcceptable(ErrorResponse errorResponse) {
        return newNotAcceptable(errorResponse, Messages.NOT_ACCEPTABLE.toString());
    }

    public static APIResponse<Object> notAcceptable(ErrorResponse errorResponse, String message) {
        return newNotAcceptable(errorResponse, message);
    }

    public static APIResponse<Object> newNotAcceptable(ErrorResponse errorResponse, String message) {
        return createAPIResponse(false, HttpStatus.NOT_ACCEPTABLE.value(), null, message, errorResponse);
    }

    private static <R> APIResponse<R> createAPIResponse(boolean success,
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

    public static APIResponse<?> ok(String message) {
        return newOk(null, message);
    }

    public enum Messages {

        BAD_REQUEST("Bad request"),
        UNAUTHORIZED("Unauthorized access"),
        FORBIDDEN("Access is forbidden"),
        NOT_FOUND("The requested resource could not be found"),
        CONFLICT("Conflict detected"),
        NOT_ACCEPTABLE("Not acceptable"),
        INTERNAL_SERVER_ERROR("An internal server error occurred. Please try again later"),
        SERVICE_UNAVAILABLE("The service is currently unavailable. Please try again later"),
        ACCESS_DENIED("Access denied due to insufficient rights"),
        UNSUPPORTED_MEDIA_TYPE("Unsupported media type"),
        TOO_MANY_REQUESTS("Rate limit exceeded"),
        DEPENDENCY_FAILURE("An external dependency failed. Please retry later"),
        INVALID_CREDENTIALS("Invalid credentials provided"),
        INSUFFICIENT_BALANCE("Insufficient balance for this operation"),
        REQUEST_PROCESSED("The request was processed successfully"),
        DATA_RETRIEVED("Data has been retrieved successfully"),
        RESOURCE_CREATED("Resource created successfully"),
        RESOURCE_UPDATED("Resource updated successfully"),
        RESOURCE_DELETED("Resource deleted successfully"),
        OPERATION_COMPLETED("Operation completed successfully"),
        REQUEST_ACCEPTED("Request accepted and is being processed"),
        AUTH_SUCCESS("Authentication succeeded"),
        INVALID_REQUEST("The request is invalid"),
        INVALID_INPUT("Provided input is invalid"),
        MISSING_FIELD("A required field is missing"),
        TOKEN_EXPIRED("Your session token has expired"),
        RESOURCE_NOT_FOUND("Requested resource was not found"),
        DUPLICATE_RESOURCE("Resource already exists"),
        INVALID_STATE("Operation not allowed in the current state");

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
            VALIDATION_FAILED,
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
