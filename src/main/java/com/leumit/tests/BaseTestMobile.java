package com.leumit.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.leumit.config.ConfigManager;
import com.leumit.context.TestContext;
import com.leumit.drivers.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BaseTestMobile - Base class for all mobile tests
 */
public class BaseTestMobile {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTestMobile.class);
    protected static ExtentReports extentReports;
    protected final ConfigManager config = ConfigManager.getInstance();
    protected TestContext context;
    protected AppiumDriver driver;

    @BeforeSuite
    public void setupSuite() {
        // Initialize ExtentReports
        String reportPath = "target/extent-reports/";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportName = reportPath + "mobile_report_" + timestamp + ".html";
        
        // Create directory if it doesn't exist
        new File(reportPath).mkdirs();
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportName);
        sparkReporter.config().setDocumentTitle("Leumit Mobile Test Report");
        sparkReporter.config().setReportName("Mobile Automation Tests");
        
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReports.setSystemInfo("Platform", "Android"); // Can be parameterized based on config
        
        logger.info("ExtentReports initialized with report: {}", reportName);
    }

    @BeforeMethod
    public void setupTest(Method method) {
        // Initialize TestContext and set platform to mobile
        context = TestContext.getInstance();
        context.setPlatform("mobile");
        
        // Set device name
        String deviceName = config.getProperty("deviceNameAndroid");
        context.setDeviceName(deviceName);
        
        // Create ExtentTest for this test method
        String testName = method.getName();
        ExtentTest test = extentReports.createTest(testName + " (" + deviceName + ")");
        context.setExtentTest(test);
        
        // Initialize Appium Driver
        driver = DriverManager.getMobileDriver();
        
        logger.info("Starting mobile test: {} on device: {}", testName, deviceName);
    }

    @AfterMethod
    public void tearDownTest(ITestResult result) {
        // Log test result
        ExtentTest test = context.getExtentTest();
        if (test != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                test.fail("Test failed: " + result.getThrowable().getMessage());
                logger.error("Test failed: {}", result.getThrowable().getMessage(), result.getThrowable());
            } else if (result.getStatus() == ITestResult.SKIP) {
                test.skip("Test skipped: " + result.getThrowable().getMessage());
                logger.info("Test skipped: {}", result.getThrowable().getMessage());
            }
        }
        
        // Reset TestContext for next test
        context.reset();
        
        logger.info("Mobile test completed with status: {}", getTestStatusName(result.getStatus()));
    }

    @AfterSuite
    public void tearDownSuite() {
        // Close the Appium driver
        DriverManager.closeMobileDriver();
        
        // Flush ExtentReports
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed and completed.");
        }
    }

    /**
     * Get test status name from status code
     * @param status TestNG status code
     * @return Status name
     */
    private String getTestStatusName(int status) {
        return switch (status) {
            case ITestResult.SUCCESS -> "SUCCESS";
            case ITestResult.FAILURE -> "FAILURE";
            case ITestResult.SKIP -> "SKIP";
            default -> "UNKNOWN";
        };
    }
}