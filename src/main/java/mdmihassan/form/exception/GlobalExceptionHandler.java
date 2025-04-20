package mdmihassan.form.exception;

import mdmihassan.form.model.ApiError;
import mdmihassan.form.model.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ApiError apiError = new ApiError();
        apiError.setCode(ApiError.ErrorCode.INVALID_INPUT);
        apiError.setType(ApiError.ErrorType.VALIDATION_ERROR);
        apiError.setMessage("Request validation failed");
        apiError.setDetails(getBindingErrorDetails(ex.getBindingResult()));
        return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(apiError));
    }

    private Object getBindingErrorDetails(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiError apiError = new ApiError();
        apiError.setType(ApiError.ErrorType.NOT_FOUND_ERROR);
        apiError.setCode(ApiError.ErrorCode.RESOURCE_NOT_FOUND);
        apiError.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound(apiError));
    }

    @ExceptionHandler(InvalidActionException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidActionException(InvalidActionException e) {
        ApiError apiError = new ApiError();
        apiError.setType(ApiError.ErrorType.UNSUPPORTED_OPERATION);
        apiError.setCode(ApiError.ErrorCode.INVALID_INPUT);
        apiError.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound(apiError));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        ApiError.GenericDetails details = new ApiError.GenericDetails();
        details.setMessage(e.getMessage());

        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setCode(ApiError.ErrorCode.INVALID_CREDENTIALS);
        apiError.setType(ApiError.ErrorType.AUTHENTICATION_ERROR);
        apiError.setDetails(details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.notFound(apiError));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedActionException(UnauthorizedActionException e) {
        return getAuthorizationErrorResponse(e.getMessage());
    }

    private ResponseEntity<ApiResponse<Object>> getAuthorizationErrorResponse(String message) {
        ApiError apiError = new ApiError();
        apiError.setMessage(message);
        apiError.setType(ApiError.ErrorType.AUTHORIZATION_ERROR);
        apiError.setCode(ApiError.ErrorCode.INVALID_CREDENTIALS);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.unauthorized(apiError));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        return getAuthorizationErrorResponse(e.getMessage());
    }


    @ExceptionHandler(ResourceCreationFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceCreationFailedException(ResourceCreationFailedException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setType(ApiError.ErrorType.BUSINESS_ERROR);
        apiError.setCode(ApiError.ErrorCode.INVALID_INPUT);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(ApiResponse.badRequest(apiError));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setType(ApiError.ErrorType.BUSINESS_ERROR);
        apiError.setCode(ApiError.ErrorCode.INVALID_INPUT);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.badRequest(apiError));
    }

    @ExceptionHandler(ResourceUpdateFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceUpdateFailedException(ResourceUpdateFailedException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setType(ApiError.ErrorType.BUSINESS_ERROR);
        apiError.setCode(ApiError.ErrorCode.INVALID_INPUT);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value())
                .body(ApiResponse.badRequest(apiError));
    }

    @ExceptionHandler(ResourceDeleteFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceDeleteFailedException(ResourceDeleteFailedException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setType(ApiError.ErrorType.BUSINESS_ERROR);
        apiError.setCode(ApiError.ErrorCode.INVALID_INPUT);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value())
                .body(ApiResponse.badRequest(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setCode(ApiError.ErrorCode.INTERNAL_SERVER_ERROR);
        apiError.setType(ApiError.ErrorType.INTERNAL_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.internalServerError(apiError));
    }

}
