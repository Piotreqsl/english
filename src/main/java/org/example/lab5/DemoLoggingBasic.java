package org.example.lab5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Demo program showing basic Log4j 2 usage without configuration file (Task 1).
 */
public class DemoLoggingBasic {
    private static final Logger log = LogManager.getLogger(DemoLoggingBasic.class);
    
    public static void main(String[] args) {
        log.trace("TRACE message (very detailed)");
        log.debug("DEBUG message (for developers)");
        log.info("INFO message (high level information)");
        log.warn("WARN message (something looks strange)");
        log.error("ERROR message (operation failed)");
        log.fatal("FATAL message (application about to crash)");
    }
}
