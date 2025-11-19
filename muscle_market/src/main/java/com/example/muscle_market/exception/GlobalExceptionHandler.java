package com.example.muscle_market.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException exception) {
        ErrorResponse res = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException exception) {
        ErrorResponse res = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    } 

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException exception) {
        ErrorResponse res = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}
