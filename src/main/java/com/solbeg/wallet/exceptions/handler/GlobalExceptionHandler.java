package com.solbeg.wallet.exceptions.handler;


import com.solbeg.wallet.exceptions.error.FieldValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Caught MethodArgumentNotValidException: {}", ex.getMessage());
        BindingResult result = ex.getBindingResult();
        final FieldValidationError error = FieldValidationError.builder()
                .status(BAD_REQUEST)
                .message("Field validation error")
                .build();
        result.getFieldErrors().forEach(fieldError -> error
                .addFieldError(fieldError.getObjectName(),
                        fieldError.getField(),
                        fieldError.getDefaultMessage())
        );
        return new ResponseEntity<>(error, error.getStatus());
    }

}
