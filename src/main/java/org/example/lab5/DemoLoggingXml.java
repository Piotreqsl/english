package org.example.lab5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Demo program showing Log4j 2 with XML configuration (Task 1).
 */
public class DemoLoggingXml {
    private static final Logger log = LogManager.getLogger(DemoLoggingXml.class);
    
    public static void main(String[] args) {
        log.info("Log4j2 configured using log4j2.xml");
        log.debug("This DEBUG message will be printed only if level <= DEBUG");
        log.trace("This TRACE message is very detailed");
        log.warn("This is a warning message");
        log.error("This is an error message");
    }
}
