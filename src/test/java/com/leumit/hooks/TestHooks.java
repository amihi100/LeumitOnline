package com.leumit.hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.leumit.config.ConfigManager;
import com.leumit.context.TestContext;
import com.leumit.drivers.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.AfterStep;
import com.aventstack.extentreports.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TestHooks - Cucumber hooks for setup and teardown
 */
public class TestHooks {
    private static final Logger logger = LoggerFactory.getLogger(TestHooks.class);
    private static ExtentReports extentReports;
    private final TestContext context = TestContext.getInstance();
    private final ConfigManager config = ConfigManager.getInstance();
    
    // We'll use a ConcurrentHashMap to store features by their URI
    private static final Map<String, ExtentTest> featureMap = new ConcurrentHashMap<>();
    
    // Track which scenarios have been processed by URI and scenario name
    private static final Map<String, Boolean> processedScenarios = new ConcurrentHashMap<>();
    
    // Track which features have browsers initialized
    private static final Set<String> initializedFeatures = ConcurrentHashMap.newKeySet();
    
    // Use a lock to synchronize feature node creation
    private static final ReentrantLock featureLock = new ReentrantLock();

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
        sparkReporter.config().setTheme(Theme.STANDARD);
        
        // Configure report to combine tests by feature
        sparkReporter.config().setTimelineEnabled(false);
        
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        
        logger.info("ExtentReports initialized with report: {}", reportName);
    }

    @Before("@web")
    public void beforeWebScenario(Scenario scenario) {
        // Set platform to web
        context.setPlatform("web");
        
        // Get feature URI and name 
        String featureUri = scenario.getUri().toString();
        String featureName = extractFeatureName(featureUri);
        
        // Debug logging to understand our feature tracking
        logger.info("Checking if feature has a browser: {}", featureUri);
        logger.info("Feature initialized status: {}", DriverManager.hasFeatureBrowser(featureUri));
        
        // Initialize browser only once per feature
        if (!DriverManager.hasFeatureBrowser(featureUri)) {
            logger.info("Initializing browser for feature: {}", featureName);
            DriverManager.initializeDriverForFeature(featureUri);
            logger.info("Feature is now initialized: {}", DriverManager.hasFeatureBrowser(featureUri));
        } else {
            logger.info("Browser already initialized for feature: {}", featureName);
        }
        
        // Create a unique key for each scenario to ensure it's only processed once
        String scenarioKey = featureUri + ":" + scenario.getName();
        
        // Check if scenario has been processed already - in case of retries
        if (processedScenarios.containsKey(scenarioKey)) {
            logger.info("Scenario already processed: {}", scenarioKey);
            return;
        }
        
        // Get or create feature test - using synchronized method for thread safety
        ExtentTest featureTest = getFeatureTestSynchronized(featureUri, featureName);
        
        // Create scenario test node as child of feature
        ExtentTest scenarioNode = featureTest.createNode(scenario.getName());
        context.setExtentTest(scenarioNode);
        
        // Add tags to report
        scenario.getSourceTagNames().forEach(tag -> scenarioNode.assignCategory(tag));
        
        // Save scenario to context
        context.setScenario(scenario);
        
        // Mark as processed
        processedScenarios.put(scenarioKey, true);
        
        logger.info("Starting web scenario: {} in feature: {}", scenario.getName(), featureName);
    }

    @Before("@mobile")
    public void beforeMobileScenario(Scenario scenario) {
        // Set platform to mobile
        context.setPlatform("mobile");
        
        // Set device name
        String deviceName = config.getProperty("deviceNameAndroid");
        context.setDeviceName(deviceName);
        
        // Get feature URI and name
        String featureUri = scenario.getUri().toString();
        String featureName = extractFeatureName(featureUri);
        
        // Create a unique key for each scenario to ensure it's only processed once
        String scenarioKey = featureUri + ":" + scenario.getName();
        
        // Check if scenario has been processed already - in case of retries
        if (processedScenarios.containsKey(scenarioKey)) {
            logger.info("Scenario already processed: {}", scenarioKey);
            return;
        }
        
        // Get or create feature test - using synchronized method for thread safety
        ExtentTest featureTest = getFeatureTestSynchronized(featureUri, featureName);
        
        // Create scenario test node as child of feature
        ExtentTest scenarioNode = featureTest.createNode(scenario.getName() + " (" + deviceName + ")");
        context.setExtentTest(scenarioNode);
        
        // Add tags to report
        scenario.getSourceTagNames().forEach(tag -> scenarioNode.assignCategory(tag));
        
        // Save scenario to context
        context.setScenario(scenario);
        
        // Mark as processed
        processedScenarios.put(scenarioKey, true);
        
        logger.info("Starting mobile scenario: {} on device: {}", scenario.getName(), deviceName);
    }
    
    /**
     * Extract feature name from feature path
     * @param featurePath Feature file path
     * @return Formatted feature name
     */
    private String extractFeatureName(String featurePath) {
        // Debug logging to understand what's happening
        logger.info("Feature path from scenario: {}", featurePath);
        
        // Extract filename from path and convert to title case
        if (featurePath != null && featurePath.contains("/")) {
            String fileName = featurePath.substring(featurePath.lastIndexOf('/') + 1);
            logger.info("Extracted fileName: {}", fileName);
            
            if (fileName.endsWith(".feature")) {
                fileName = fileName.substring(0, fileName.indexOf(".feature"));
                logger.info("After removing .feature: {}", fileName);
            }
            // Convert snake_case to Title Case
            String formattedName = formatFeatureName(fileName);
            logger.info("Formatted feature name: {}", formattedName);
            return formattedName;
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
     * Thread-safe method to get or create a feature test node
     * Uses ReentrantLock to ensure only one thread can create a feature at a time
     * 
     * @param featureUri Feature URI used as the key
     * @param featureName Display name for the feature
     * @return ExtentTest for the feature
     */
    private ExtentTest getFeatureTestSynchronized(String featureUri, String featureName) {
        // First try to get without locking (optimistic)
        ExtentTest existingTest = featureMap.get(featureUri);
        if (existingTest != null) {
            return existingTest;
        }
        
        // If not found, use a lock to prevent race conditions
        featureLock.lock();
        try {
            // Check again in case another thread created it while we were waiting
            existingTest = featureMap.get(featureUri);
            if (existingTest != null) {
                return existingTest;
            }
            
            // Create new feature node
            ExtentTest featureTest = extentReports.createTest(featureName);
            featureMap.put(featureUri, featureTest);
            return featureTest;
        } finally {
            featureLock.unlock();
        }
    }

    @After(value = "@web", order = 10)
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
        
        // Only reset context, don't close browser yet
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
        DriverManager.closeAllFeatureBrowsers();
        
        // Flush ExtentReports
        if (extentReports != null) {
            extentReports.flush();
            // Print out how many feature nodes were created
            logger.info("Created {} feature nodes in the report", featureMap.size());
            // Print each feature URI for debugging
            featureMap.keySet().forEach(uri -> logger.info("Feature URI: {}", uri));
            logger.info("ProcessedScenarios size: {}", processedScenarios.size());
            logger.info("ExtentReports flushed and completed.");
        }
        
        // Clear cache
        featureMap.clear();
        processedScenarios.clear();
    }

    @BeforeStep
    public void beforeStep(Scenario scenario) {
        // Get current step text
        String stepText = extractStepText(scenario);
        if (stepText != null && !stepText.isEmpty()) {
            // Log step name as INFO in report - simple version
            ExtentTest test = context.getExtentTest();
            if (test != null) {
                test.log(Status.INFO, "STEP: " + stepText);
            }
            logger.info("Executing step: {}", stepText);
        }
    }
    
    @AfterStep
    public void afterStep(Scenario scenario) {
        // Optionally log step completion
        if (scenario.isFailed()) {
            String stepText = extractStepText(scenario);
            if (stepText != null && !stepText.isEmpty()) {
                ExtentTest test = context.getExtentTest();
                if (test != null) {
                    test.log(Status.FAIL, "FAILED STEP: " + stepText);
                }
            }
        }
    }
    
    /**
     * Extract the current step text from the scenario
     * @param scenario Cucumber scenario
     * @return Step text or empty string if not available
     */
    private String extractStepText(Scenario scenario) {
        try {
            // Get the scenario's string representation
            String scenarioString = scenario.toString();
            
            // Look for step text pattern
            if (scenarioString.contains("Step [")) {
                int stepStart = scenarioString.lastIndexOf("Step [") + 6; // +6 to skip "Step ["
                int stepEnd = scenarioString.indexOf("]", stepStart);
                
                if (stepEnd > stepStart) {
                    return scenarioString.substring(stepStart, stepEnd);
                }
            }
            
            return "";
        } catch (Exception e) {
            logger.error("Error extracting step text", e);
            return "";
        }
    }
} 