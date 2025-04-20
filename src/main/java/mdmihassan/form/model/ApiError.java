package mdmihassan.form.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private ErrorCode code;
    private String message;
    private ErrorType type;
    private Object details;

    public enum ErrorType {
        VALIDATION_ERROR,
        AUTHENTICATION_ERROR,
        AUTHORIZATION_ERROR,
        BUSINESS_ERROR,
        NOT_FOUND_ERROR,
        CONFLICT_ERROR,
        INTERNAL_ERROR,
        THIRD_PARTY_ERROR,
        RATE_LIMIT_ERROR,
        UNSUPPORTED_OPERATION
    }

    public enum ErrorCode {

        INVALID_INPUT("Invalid input provided"),
        MISSING_FIELD("A required field is missing"),
        INVALID_CREDENTIALS("Invalid username or password"),
        TOKEN_EXPIRED("Authentication token has expired"),
        ACCESS_DENIED("Access is denied"),
        RESOURCE_NOT_FOUND("Requested resource was not found"),
        DUPLICATE_RESOURCE("Resource already exists"),
        INSUFFICIENT_BALANCE("Insufficient balance to complete operation"),
        INTERNAL_SERVER_ERROR("An unexpected error occurred"),
        SERVICE_UNAVAILABLE("Dependent service is unavailable"),
        TOO_MANY_REQUESTS("Rate limit exceeded"),
        UNSUPPORTED_MEDIA_TYPE("Unsupported media type"),
        INVALID_STATE("Operation not allowed in the current state"),
        DEPENDENCY_FAILURE("A required external dependency failed");

        private final String defaultMessage;

        ErrorCode(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }

    @Data
    public static class GenericDetails {
        private boolean success;
        private String message;
    }


}