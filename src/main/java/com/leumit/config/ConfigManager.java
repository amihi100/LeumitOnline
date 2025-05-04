package com.leumit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * ConfigManager - Singleton class to manage configuration properties
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();
        loadDefaultProperties();
        
        // Load environment-specific properties if environment is set
        String env = System.getProperty("env");
        if (env != null && !env.isEmpty()) {
            loadEnvironmentProperties(env);
        }
        
        // Override with system properties
        loadSystemProperties();
    }

    /**
     * Get the singleton instance of ConfigManager
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Load default properties from config.properties
     */
    private void loadDefaultProperties() {
        loadPropertiesFromFile("src/test/resources/config/config.properties");
    }

    /**
     * Load environment-specific properties
     * @param env Environment name
     */
    private void loadEnvironmentProperties(String env) {
        String envConfigPath = "src/test/resources/config/" + env + ".properties";
        loadPropertiesFromFile(envConfigPath);
    }

    /**
     * Load properties from a file
     * @param filePath Path to properties file
     */
    private void loadPropertiesFromFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            logger.warn("Properties file not found: {}", filePath);
            return;
        }

        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
            logger.info("Loaded properties from: {}", filePath);
        } catch (IOException e) {
            logger.error("Error loading properties from: {}", filePath, e);
        }
    }

    /**
     * Override properties with system properties
     */
    private void loadSystemProperties() {
        Properties systemProperties = System.getProperties();
        for (String name : systemProperties.stringPropertyNames()) {
            properties.setProperty(name, systemProperties.getProperty(name));
        }
    }

    /**
     * Get a property value
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get a property value with default
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default if not found
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get a property as an integer
     * @param key Property key
     * @param defaultValue Default value if property not found or not a valid integer
     * @return Property value as integer or default
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer property: {} = {}", key, value);
            return defaultValue;
        }
    }

    /**
     * Get a property as a boolean
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value as boolean or default
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
} 