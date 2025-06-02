package mdmihassan.web.api.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import mdmihassan.web.api.*;
import mdmihassan.web.api.APIResponse.ErrorResponse;
import mdmihassan.web.api.validation.FieldValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ErrorResponse.ErrorCode.VALIDATION_FAILED);
        errorResponse.setMessage("Request validation failed");
        errorResponse.setDetails(buildFieldValidationErrors(ex.getBindingResult()));
        return ResponseEntity.badRequest()
                .body(APIResponse.badRequest(errorResponse));
    }

    private List<FieldValidationError> buildFieldValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> FieldValidationError.builder()
                        .field(fieldError.getField())
                        .rejectedValue(fieldError.getRejectedValue())
                        .defaultMessage(fieldError.getDefaultMessage())
                        .code(fieldError.getCode())
                        .build())
                .toList();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ErrorResponse.ErrorCode.NOT_FOUND);
        errorResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.notFound(errorResponse));
    }

    @ExceptionHandler(InvalidActionException.class)
    public ResponseEntity<APIResponse<Object>> handleInvalidActionException(InvalidActionException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ErrorResponse.ErrorCode.UNSUPPORTED_OPERATION);
        errorResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.notFound(errorResponse));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        ErrorResponse.GenericDetails details = new ErrorResponse.GenericDetails();
        details.setMessage(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setCode(ErrorResponse.ErrorCode.AUTHENTICATION_FAILED);
        errorResponse.setDetails(details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.badRequest(errorResponse, APIResponse.Messages.INVALID_CREDENTIALS.toString()));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<APIResponse<Object>> handleUnauthorizedActionException(UnauthorizedActionException e) {
        return getAuthorizationErrorResponse(e.getMessage());
    }

    private ResponseEntity<APIResponse<Object>> getAuthorizationErrorResponse(String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(message);
        errorResponse.setCode(ErrorResponse.ErrorCode.AUTHORIZATION_FAILED);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(APIResponse.unauthorized(errorResponse));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<APIResponse<Object>> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        return getAuthorizationErrorResponse(e.getMessage());
    }


    @ExceptionHandler(ResourceCreationFailedException.class)
    public ResponseEntity<APIResponse<Object>> handleResourceCreationFailedException(ResourceCreationFailedException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setCode(ErrorResponse.ErrorCode.BUSINESS_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.badRequest(errorResponse));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<APIResponse<Object>> handleDuplicateResourceException(DuplicateResourceException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setCode(ErrorResponse.ErrorCode.BUSINESS_ERROR);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(APIResponse.conflict(errorResponse));
    }

    @ExceptionHandler(ResourceUpdateFailedException.class)
    public ResponseEntity<APIResponse<Object>> handleResourceUpdateFailedException(ResourceUpdateFailedException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setCode(ErrorResponse.ErrorCode.BUSINESS_ERROR);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value())
                .body(APIResponse.notAcceptable(errorResponse));
    }

    @ExceptionHandler(ResourceDeleteFailedException.class)
    public ResponseEntity<APIResponse<Object>> handleResourceDeleteFailedException(ResourceDeleteFailedException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setCode(ErrorResponse.ErrorCode.BUSINESS_ERROR);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value())
                .body(APIResponse.notAcceptable(errorResponse));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Throwable rootCause = e.getMostSpecificCause();
        String message = switch (rootCause) {
            case InvalidFormatException ife ->
                    String.format("Invalid format for field '%s'. Expected type: %s", ife.getPathReference(), ife.getTargetType().getSimpleName());
            case UnrecognizedPropertyException upe -> String.format("Unknown field: '%s'", upe.getPropertyName());
            case MismatchedInputException mie ->
                    String.format("Type mismatch at '%s': %s", mie.getPathReference(), mie.getOriginalMessage());
            default -> "Malformed request";
        };

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(message);
        errorResponse.setCode(ErrorResponse.ErrorCode.INVALID_INPUT);

        return ResponseEntity
                .badRequest()
                .body(APIResponse.badRequest(errorResponse));
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<APIResponse<Object>> handleHttpMessageConversionException(HttpMessageConversionException e) {
        Throwable rootCause = e.getMostSpecificCause();
        String message = switch (rootCause) {
            case InvalidDefinitionException ide -> String.format("Malformed fields: '%s'", ide.getPath()
                    .stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining()));
            default -> "Malformed request";
        };

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(message);
        errorResponse.setCode(ErrorResponse.ErrorCode.INVALID_INPUT);

        return ResponseEntity
                .badRequest()
                .body(APIResponse.badRequest(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("An unexpected error occurred");
        errorResponse.setCode(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.internalServerError(errorResponse));
    }

}
