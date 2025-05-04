package com.leumit.context;

import com.aventstack.extentreports.ExtentTest;
import io.cucumber.java.Scenario;

import java.util.HashMap;
import java.util.Map;

/**
 * TestContext - Thread-safe singleton class for managing test data
 * Uses ThreadLocal to maintain thread safety for parallel execution
 */
public class TestContext {
    private static final ThreadLocal<TestContext> threadLocalInstance = new ThreadLocal<>();
    private final ThreadLocal<Map<String, Object>> testData = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Scenario> scenario = new ThreadLocal<>();
    private final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private final ThreadLocal<String> platform = ThreadLocal.withInitial(() -> "web");
    private final ThreadLocal<String> deviceName = new ThreadLocal<>();

    private TestContext() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the singleton instance of TestContext
     * @return TestContext instance for the current thread
     */
    public static synchronized TestContext getInstance() {
        if (threadLocalInstance.get() == null) {
            threadLocalInstance.set(new TestContext());
        }
        return threadLocalInstance.get();
    }

    /**
     * Clear all data for the current thread
     */
    public void reset() {
        testData.get().clear();
        scenario.remove();
        extentTest.remove();
        platform.remove();
        deviceName.remove();
    }

    /**
     * Set the current Cucumber scenario
     * @param scenario Cucumber scenario
     */
    public void setScenario(Scenario scenario) {
        this.scenario.set(scenario);
    }

    /**
     * Get the current Cucumber scenario
     * @return Current Cucumber scenario
     */
    public Scenario getScenario() {
        return scenario.get();
    }

    /**
     * Set the ExtentTest instance for current thread
     * @param test ExtentTest instance
     */
    public void setExtentTest(ExtentTest test) {
        this.extentTest.set(test);
    }

    /**
     * Get the ExtentTest instance for current thread
     * @return ExtentTest instance
     */
    public ExtentTest getExtentTest() {
        return extentTest.get();
    }

    /**
     * Set the platform (web or mobile)
     * @param platform Platform name
     */
    public void setPlatform(String platform) {
        this.platform.set(platform);
    }

    /**
     * Get the current platform
     * @return Current platform (web or mobile)
     */
    public String getPlatform() {
        return platform.get();
    }

    /**
     * Set the device name for mobile tests
     * @param deviceName Device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName.set(deviceName);
    }

    /**
     * Get the device name for mobile tests
     * @return Device name
     */
    public String getDeviceName() {
        return deviceName.get();
    }

    /**
     * Store data in the context
     * @param key Data key
     * @param value Data value
     */
    public void setAttribute(String key, Object value) {
        testData.get().put(key, value);
    }

    /**
     * Retrieve data from the context
     * @param key Data key
     * @param <T> Type of data to retrieve
     * @return Data value
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) testData.get().get(key);
    }

    /**
     * Remove data from the context
     * @param key Data key
     */
    public void removeAttribute(String key) {
        testData.get().remove(key);
    }
} 