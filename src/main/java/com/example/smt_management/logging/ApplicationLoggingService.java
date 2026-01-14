package com.example.smt_management.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApplicationLoggingService {

    private static final Logger log =
            LoggerFactory.getLogger("APPLICATION_LOGGER");

    public void info(String message) {
        log.info(message);
    }

    public void info(String message, Object context) {
        log.info("{} | context={}", message, context);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Throwable ex) {
        log.error(message, ex);
    }
}