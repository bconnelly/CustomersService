package com.fullstack.customerservice.Utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomerExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String entityNotFoundexceptionHandler(EntityNotFoundException exception){
        log.error(String.format("Exception: %s, message: %s", exception.getCause(), exception.getMessage()));
        return String.format("'%s': entity not found", exception.getCause());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception exception){
        log.error(String.format("Exception: %s, message: %s", exception.getCause(), exception.getMessage()));
        return String.valueOf(exception.getCause());
    }
}
