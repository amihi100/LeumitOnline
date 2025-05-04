package com.leumit.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.leumit.context.TestContext;
import com.leumit.drivers.DriverManager;
import com.microsoft.playwright.Page;
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
 * BaseTestWeb - Base class for all web tests
 */
public class BaseTestWeb {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTestWeb.class);
    protected static ExtentReports extentReports;
    protected TestContext context;
    protected Page page;

    @BeforeSuite
    public void setupSuite() {
        // Initialize ExtentReports
        String reportPath = "target/extent-reports/";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportName = reportPath + "web_report_" + timestamp + ".html";
        
        // Create directory if it doesn't exist
        new File(reportPath).mkdirs();
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportName);
        sparkReporter.config().setDocumentTitle("Leumit Web Test Report");
        sparkReporter.config().setReportName("Web Automation Tests");
        
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReports.setSystemInfo("Browser", "Chrome"); // Can be parameterized based on config
        
        logger.info("ExtentReports initialized with report: {}", reportName);
    }

    @BeforeMethod
    public void setupTest(Method method) {
        // Initialize TestContext and set platform to web
        context = TestContext.getInstance();
        context.setPlatform("web");
        
        // Create ExtentTest for this test method
        String testName = method.getName();
        ExtentTest test = extentReports.createTest(testName);
        context.setExtentTest(test);
        
        // Initialize Playwright Page
        page = DriverManager.getPage();
        
        logger.info("Starting web test: {}", testName);
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
        
        logger.info("Web test completed with status: {}", getTestStatusName(result.getStatus()));
    }

    @AfterSuite
    public void tearDownSuite() {
        // Close the browser and Playwright
        DriverManager.closeAllDrivers();
        
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