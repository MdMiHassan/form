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
public class ApiResponse<T> {

    private boolean success;
    private Instant timestamp;
    private int status;
    private String message;
    private T data;
    private ApiError error;

    public static <R> ApiResponse<R> ok(R response, String message) {
        return newOk(response, message);
    }

    public static <R> ApiResponse<R> ok(R response) {
        return newOk(response, ApiMessages.REQUEST_PROCESSED.toString());
    }

    private static <R> ApiResponse<R> newOk(R response, String message) {
        return createApiResponse(true, HttpStatus.OK.value(), response, message, null);
    }

    public static ApiResponse<Object> badRequest(ApiError apiError) {
        return newBadRequest(apiError, ApiMessages.BAD_REQUEST.toString());
    }

    public static ApiResponse<Object> badRequest(ApiError apiError, String message) {
        return newBadRequest(apiError, message);
    }

    public static ApiResponse<Object> newBadRequest(ApiError apiError, String message) {
        return createApiResponse(false, HttpStatus.BAD_REQUEST.value(), null, message, apiError);
    }

    public static ApiResponse<Object> unauthorized(ApiError apiError) {
        return createApiResponse(false, HttpStatus.UNAUTHORIZED.value(), null,
                ApiMessages.UNAUTHORIZED.toString(), apiError);
    }

    public static ApiResponse<Object> forbidden(ApiError apiError) {
        return createApiResponse(false, HttpStatus.FORBIDDEN.value(), null,
                ApiMessages.FORBIDDEN.toString(), apiError);
    }

    public static ApiResponse<Object> notFound(ApiError apiError) {
        return createApiResponse(false, HttpStatus.NOT_FOUND.value(), null,
                ApiMessages.NOT_FOUND.toString(), apiError);
    }

    public static ApiResponse<Object> conflict(ApiError apiError) {
        return createApiResponse(false, HttpStatus.CONFLICT.value(), null,
                ApiMessages.CONFLICT.toString(), apiError);
    }

    public static ApiResponse<Object> internalServerError(ApiError apiError) {
        return createApiResponse(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), null,
                ApiMessages.INTERNAL_SERVER_ERROR.toString(), apiError);
    }

    private static <R> ApiResponse<R> createApiResponse(boolean success,
                                                        int code,
                                                        R response,
                                                        String message,
                                                        ApiError error) {
        ApiResponse<R> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(success);
        apiResponse.setStatus(code);
        apiResponse.setData(response);
        apiResponse.setMessage(message);
        apiResponse.setTimestamp(Instant.now());
        apiResponse.setError(error);
        return apiResponse;
    }

    public enum ApiMessages {

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
        INTERNAL_SERVER_ERROR("We are facing some problems now, we are working hard to get it fixed!");


        private final String message;

        ApiMessages(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

}
