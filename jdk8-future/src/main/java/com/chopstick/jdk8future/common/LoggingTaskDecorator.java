package com.chopstick.jdk8future.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

@Component
public class LoggingTaskDecorator implements TaskDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingTaskDecorator.class);

    @Autowired
    private AsyncUncaughtExceptionHandler exceptionHandler;

    @Override
    public Runnable decorate(Runnable runnable) {
        return () -> {
            try {
                long startedAt = System.currentTimeMillis();
                runnable.run();
                LOGGER.debug("Thread {} took {} milliseconds to complete.",
                        Thread.currentThread().getName(), System.currentTimeMillis() - startedAt);
            } catch (Exception ex) {
                exceptionHandler.handleUncaughtException(ex, runnable.getClass().getEnclosingMethod(), null);
            }
        };
    }
}
