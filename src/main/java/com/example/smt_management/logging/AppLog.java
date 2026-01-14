package com.example.smt_management.logging;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AppLog implements ApplicationContextAware {

    private static ApplicationLoggingService loggingService;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        loggingService = context.getBean(ApplicationLoggingService.class);
    }

    public static void info(String message) {
        loggingService.info(message);
    }

    public static void info(String message, Object context) {
        loggingService.info(message, context);
    }

    public static void warn(String message) {
        loggingService.warn(message);
    }

    public static void error(String message) {
        loggingService.error(message);
    }

    public static void error(String message, Throwable ex) {
        loggingService.error(message, ex);
    }
}
