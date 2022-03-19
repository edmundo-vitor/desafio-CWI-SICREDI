package com.desafio.edmundo.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    public static final String GENERIC_ERROR_MESSAGE = "An unexpected internal error has occurred. Please try again and if the problem persists, contact your system administrator.";

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public HttpErrorInfo handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> msg = new ArrayList<>();
        BindingResult cause = ex.getBindingResult();
        List<ObjectError> firstProblem = cause.getAllErrors();

        if (firstProblem != null) {
            firstProblem.stream().forEach(error -> {
            	msg.add(error.getDefaultMessage());
            });
        }

        return createHttpErrorInfo(msg);
    }
    
    @ResponseStatus(METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public HttpErrorInfo handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
    	List<String> msg = new ArrayList<>();
    	msg.add(ex.getMessage());
    	
    	return createHttpErrorInfo(msg);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public HttpErrorInfo handleExceptions(Exception ex) {
        log.error("Message: {}", ex);
        List<String> msg = new ArrayList<>();
        msg.add(GENERIC_ERROR_MESSAGE);
        
        return createHttpErrorInfo(msg);
    }
    
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public HttpErrorInfo handleEntityNotFoundException(EntityNotFoundException ex) {
    	List<String> errors = new ArrayList<>();
    	errors.add(ex.getMessage());
    	return createHttpErrorInfo(errors);
    }
    
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public HttpErrorInfo handleBadRequestException(BadRequestException ex) {
    	List<String> msg = new ArrayList<>();
    	msg.add(ex.getMessagePersonalized());
    	return createHttpErrorInfo(msg);
    }

    private HttpErrorInfo createHttpErrorInfo(List<String> msg) {
        return HttpErrorInfo.builder()
                .message(msg)
                .build();
    }

}
