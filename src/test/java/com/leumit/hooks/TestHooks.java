package com.leumit.hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.leumit.config.ConfigManager;
import com.leumit.context.TestContext;
import com.leumit.drivers.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * TestHooks - Cucumber hooks for setup and teardown
 */
public class TestHooks {
    private static final Logger logger = LoggerFactory.getLogger(TestHooks.class);
    private static ExtentReports extentReports;
    private final TestContext context = TestContext.getInstance();
    private final ConfigManager config = ConfigManager.getInstance();
    
    // Cache for feature tests to create hierarchy
    private static final Map<String, ExtentTest> featureMap = new HashMap<>();

    @BeforeAll
    public static void beforeAll() {
        // Initialize ExtentReports
        String reportPath = "target/extent-reports/";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportName = reportPath + "cucumber_report_" + timestamp + ".html";
        
        // Create directory if it doesn't exist
        new File(reportPath).mkdirs();
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportName);
        sparkReporter.config().setDocumentTitle("Leumit Automation Test Report");
        sparkReporter.config().setReportName("Cucumber BDD Tests");
        
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        
        logger.info("ExtentReports initialized with report: {}", reportName);
    }

    @Before("@web")
    public void beforeWebScenario(Scenario scenario) {
        // Set platform to web
        context.setPlatform("web");
        
        // Get feature name and create a hierarchical report structure
        String featurePath = scenario.getUri().toString();
        String featureName = extractFeatureName(featurePath);
        
        // Get or create feature test
        ExtentTest featureTest = getFeatureTest(featureName);
        
        // Create scenario test node as child of feature
        ExtentTest scenarioNode = featureTest.createNode(scenario.getName());
        context.setExtentTest(scenarioNode);
        
        // Add tags to report
        scenario.getSourceTagNames().forEach(tag -> scenarioNode.assignCategory(tag));
        
        // Save scenario to context
        context.setScenario(scenario);
        
        logger.info("Starting web scenario: {}", scenario.getName());
    }

    @Before("@mobile")
    public void beforeMobileScenario(Scenario scenario) {
        // Set platform to mobile
        context.setPlatform("mobile");
        
        // Set device name
        String deviceName = config.getProperty("deviceNameAndroid");
        context.setDeviceName(deviceName);
        
        // Get feature name and create a hierarchical report structure
        String featurePath = scenario.getUri().toString();
        String featureName = extractFeatureName(featurePath);
        
        // Get or create feature test
        ExtentTest featureTest = getFeatureTest(featureName);
        
        // Create scenario test node as child of feature
        ExtentTest scenarioNode = featureTest.createNode(scenario.getName() + " (" + deviceName + ")");
        context.setExtentTest(scenarioNode);
        
        // Add tags to report
        scenario.getSourceTagNames().forEach(tag -> scenarioNode.assignCategory(tag));
        
        // Save scenario to context
        context.setScenario(scenario);
        
        logger.info("Starting mobile scenario: {} on device: {}", scenario.getName(), deviceName);
    }
    
    /**
     * Extract feature name from feature path
     * @param featurePath Feature file path
     * @return Formatted feature name
     */
    private String extractFeatureName(String featurePath) {
        // Extract filename from path and convert to title case
        if (featurePath != null && featurePath.contains("/")) {
            String fileName = featurePath.substring(featurePath.lastIndexOf('/') + 1);
            if (fileName.endsWith(".feature")) {
                fileName = fileName.substring(0, fileName.indexOf(".feature"));
            }
            // Convert snake_case to Title Case
            return formatFeatureName(fileName);
        }
        return "Unknown Feature";
    }
    
    /**
     * Format feature name to title case
     * @param name Raw feature name
     * @return Formatted feature name
     */
    private String formatFeatureName(String name) {
        // Replace underscores with spaces
        name = name.replace('_', ' ');
        
        // Capitalize first letter of each word
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : name.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }
    
    /**
     * Get or create a feature test node
     * @param featureName Feature name
     * @return ExtentTest for the feature
     */
    private ExtentTest getFeatureTest(String featureName) {
        return featureMap.computeIfAbsent(featureName, 
            name -> extentReports.createTest(name));
    }

    @After("@web")
    public void afterWebScenario(Scenario scenario) {
        // Get ExtentTest
        ExtentTest test = context.getExtentTest();
        
        // Log scenario status
        if (scenario.isFailed() && test != null) {
            test.fail("Scenario failed");
            logger.error("Scenario failed: {}", scenario.getName());
        } else if (test != null) {
            test.pass("Scenario passed");
        }
        
        // Close the web driver
        DriverManager.closeBrowser();
        
        // Reset context for next scenario
        context.reset();
        
        logger.info("Web scenario completed with status: {}", scenario.getStatus());
    }

    @After("@mobile")
    public void afterMobileScenario(Scenario scenario) {
        // Get ExtentTest
        ExtentTest test = context.getExtentTest();
        
        // Log scenario status
        if (scenario.isFailed() && test != null) {
            test.fail("Scenario failed");
            logger.error("Scenario failed: {}", scenario.getName());
        } else if (test != null) {
            test.pass("Scenario passed");
        }
        
        // Close the mobile driver
        DriverManager.closeMobileDriver();
        
        // Reset context for next scenario
        context.reset();
        
        logger.info("Mobile scenario completed with status: {}", scenario.getStatus());
    }

    @AfterAll
    public static void afterAll() {
        // Close all drivers
        DriverManager.closeAllDrivers();
        
        // Flush ExtentReports
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed and completed.");
        }
        
        // Clear cache
        featureMap.clear();
    }
} 