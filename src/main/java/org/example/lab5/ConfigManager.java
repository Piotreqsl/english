package org.example.lab5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages application configuration using a properties file.
 */
public class ConfigManager {
    private static final Logger log = LogManager.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "console.properties";
    private Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadOrCreateConfig();
    }

    private void loadOrCreateConfig() {
        Path configPath = Paths.get(CONFIG_FILE);

        if (Files.exists(configPath)) {
            try {
                properties.load(Files.newInputStream(configPath));
                log.info("Loaded configuration from {}", CONFIG_FILE);
            } catch (IOException e) {
                log.error("Error loading configuration: {}", e.getMessage());
                setDefaults();
            }
        } else {
            setDefaults();
            saveConfig();
            log.info("Created default configuration: {}", CONFIG_FILE);
            System.out.println("[INFO] Created default configuration: " + CONFIG_FILE);
        }

        // Ensure all required keys exist
        if (!properties.containsKey("delimiter")) {
            properties.setProperty("delimiter", ";");
        }
        if (!properties.containsKey("students")) {
            properties.setProperty("students", "students.csv");
        }
        if (!properties.containsKey("groups")) {
            properties.setProperty("groups", "groups.csv");
        }
    }

    private void setDefaults() {
        properties.setProperty("delimiter", ";");
        properties.setProperty("students", "students.csv");
        properties.setProperty("groups", "groups.csv");
    }

    private void saveConfig() {
        try {
            properties.store(Files.newOutputStream(Paths.get(CONFIG_FILE)),
                "Student & Group Manager Configuration");
            log.debug("Configuration saved to {}", CONFIG_FILE);
        } catch (IOException e) {
            log.error("Error saving configuration: {}", e.getMessage());
        }
    }

    public String getDelimiter() {
        return properties.getProperty("delimiter", ";");
    }

    public String getStudentsFile() {
        return properties.getProperty("students", "students.csv");
    }

    public String getGroupsFile() {
        return properties.getProperty("groups", "groups.csv");
    }

    public void setDelimiter(String delimiter) {
        properties.setProperty("delimiter", delimiter);
        saveConfig();
    }

    public void setStudentsFile(String filename) {
        properties.setProperty("students", filename);
        saveConfig();
    }

    public void setGroupsFile(String filename) {
        properties.setProperty("groups", filename);
        saveConfig();
    }

    public void showConfig() {
        System.out.println("\n=== Current Configuration ===");
        System.out.println("Delimiter: " + getDelimiter());
        System.out.println("Students file: " + getStudentsFile());
        System.out.println("Groups file: " + getGroupsFile());
    }
}
