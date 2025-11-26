package com.mert.taskmanager.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExpectionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, ServletWebRequest request) {

        // 1. Path bilgisini çekiyoruz.Servelet diye WebRequest somut hali cascate etmemek için paratmre olark direk servlet girdim
        String path= request.getRequest().getRequestURI();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();

            return  ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
    }
    @ExceptionHandler (MethodArgumentNotValidException.class)
    public  ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e, ServletWebRequest request) {
        String path= request.getRequest().getRequestURI();
        List<String> errorMessages = new ArrayList<>();
        for (FieldError error :e.getBindingResult().getFieldErrors()) {
            errorMessages.add(error.getDefaultMessage());
        }
        String errorMessageAllInOne = String.join(";",errorMessages);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(errorMessageAllInOne)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();

        return   ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public  ResponseEntity<ErrorResponse> handleGeneralException(Exception e, ServletWebRequest request) {
        String path= request.getRequest().getRequestURI();

        ErrorResponse errorResponse=ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
        return  ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);

    }
}
