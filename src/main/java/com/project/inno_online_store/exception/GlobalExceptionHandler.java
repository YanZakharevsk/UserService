package com.project.inno_online_store.exception;

import com.project.inno_online_store.dto.response.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest httpServletRequest) {
        return createExceptionResponse(ex, httpServletRequest);
    }

    @ExceptionHandler(PaymentCardNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentCardNotFoundException(PaymentCardNotFoundException ex, HttpServletRequest httpServletRequest) {
        return createExceptionResponse(ex, httpServletRequest);
    }

    @ExceptionHandler(UserCardLimitExceededException.class)
    public ResponseEntity<ExceptionResponse> handleUserCardLimitExceededException(UserCardLimitExceededException ex, HttpServletRequest httpServletRequest){
        return createExceptionResponse(ex, httpServletRequest);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidJwtTokenException(InvalidJwtTokenException ex, HttpServletRequest request){
        return createExceptionResponse(ex, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest httpServletRequest){

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("VALIDATION_ERROR");
        response.setMessage(message);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(Instant.now());
        response.setPath(httpServletRequest.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(
            NoHandlerFoundException ex,
            HttpServletRequest httpServletRequest
    ) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("NOT_FOUND");
        response.setMessage("Endpoint not found");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setTimestamp(Instant.now());
        response.setPath(httpServletRequest.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest httpServletRequest
    ) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("METHOD_NOT_ALLOWED");
        response.setMessage("HTTP method not supported");
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        response.setTimestamp(Instant.now());
        response.setPath(httpServletRequest.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    private ResponseEntity<ExceptionResponse> createExceptionResponse(BaseException ex, HttpServletRequest httpServletRequest) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode(ex.getErrorCode());
        response.setMessage(ex.getMessage());
        response.setStatus(ex.getStatus().value());
        response.setTimestamp(Instant.now());
        response.setPath(httpServletRequest.getRequestURI());
        return new ResponseEntity<>(response, ex.getStatus());
    }
}
