package com.chopstick.jdk8future.advise;

import com.chopstick.jdk8future.common.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionException;

import static java.lang.String.format;

@ControllerAdvice
public class GenericExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        LOGGER.error(format("Unexpected exception occurred invoking async method: %s with params: %s", method, params));
        handleCompletionExceptions(new CompletionException(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleCompletionExceptions(Exception ex) {
        LOGGER.error("An async uncaught exception occurred", ex);
        return new ResponseEntity<>(new ErrorResponse("CompletionException", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
