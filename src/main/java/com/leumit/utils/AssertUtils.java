package com.leumit.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.leumit.context.TestContext;
import com.leumit.drivers.DriverManager;
import com.microsoft.playwright.Page;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * AssertUtils - Utility class for assertions with ExtentReports integration
 */
public class AssertUtils {
    private static final Logger logger = LoggerFactory.getLogger(AssertUtils.class);
    private static final String SCREENSHOT_PATH = "target/screenshots";

    static {
        // Create screenshot directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_PATH));
        } catch (Exception e) {
            logger.error("Failed to create screenshots directory", e);
        }
    }

    /**
     * Assert that a condition is true
     * @param actual Actual condition
     * @param passMessage Message for pass case
     * @param failMessage Message for fail case
     */
    public static void assertTrue(boolean actual, String passMessage, String failMessage) {
        assertTrue(actual, true, passMessage, failMessage);
    }

    /**
     * Assert that two boolean values are equal
     * @param actual Actual value
     * @param expected Expected value
     * @param passMessage Message for pass case
     * @param failMessage Message for fail case
     */
    public static void assertTrue(boolean actual, boolean expected, String passMessage, String failMessage) {
        TestContext context = TestContext.getInstance();
        ExtentTest test = context.getExtentTest();
        
        try {
            if (expected) {
                Assert.assertTrue(actual, failMessage);
            } else {
                Assert.assertFalse(actual, failMessage);
            }
            
            // Log pass in report
            if (test != null) {
                test.log(Status.PASS, passMessage);
            }
            logger.info(passMessage);
        } catch (AssertionError e) {
            // Take screenshot on failure
            String screenshotPath = captureScreenshot();
            
            // Log failure with screenshot in report
            if (test != null && screenshotPath != null) {
                test.log(Status.FAIL, failMessage, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else if (test != null) {
                test.log(Status.FAIL, failMessage);
            }
            
            logger.error(failMessage);
            throw e; // Re-throw the assertion error
        }
    }

    /**
     * Assert that two strings are equal
     * @param actual Actual string
     * @param expected Expected string
     * @param passMessage Message for pass case
     * @param failMessage Message for fail case
     */
    public static void assertEquals(String actual, String expected, String passMessage, String failMessage) {
        TestContext context = TestContext.getInstance();
        ExtentTest test = context.getExtentTest();
        
        try {
            Assert.assertEquals(actual, expected, failMessage);
            
            // Log pass in report
            if (test != null) {
                test.log(Status.PASS, passMessage);
            }
            logger.info(passMessage);
        } catch (AssertionError e) {
            // Take screenshot on failure
            String screenshotPath = captureScreenshot();
            
            // Log failure with screenshot in report
            if (test != null && screenshotPath != null) {
                test.log(Status.FAIL, failMessage + ": expected [" + expected + "] but found [" + actual + "]",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else if (test != null) {
                test.log(Status.FAIL, failMessage + ": expected [" + expected + "] but found [" + actual + "]");
            }
            
            logger.error(failMessage + ": expected [" + expected + "] but found [" + actual + "]");
            throw e; // Re-throw the assertion error
        }
    }

    /**
     * Assert that a string contains another string
     * @param actual Actual string
     * @param expected Expected substring
     * @param passMessage Message for pass case
     * @param failMessage Message for fail case
     */
    public static void assertContains(String actual, String expected, String passMessage, String failMessage) {
        TestContext context = TestContext.getInstance();
        ExtentTest test = context.getExtentTest();
        
        try {
            Assert.assertTrue(actual.contains(expected), failMessage);
            
            // Log pass in report
            if (test != null) {
                test.log(Status.PASS, passMessage);
            }
            logger.info(passMessage);
        } catch (AssertionError e) {
            // Take screenshot on failure
            String screenshotPath = captureScreenshot();
            
            // Log failure with screenshot in report
            if (test != null && screenshotPath != null) {
                test.log(Status.FAIL, failMessage + ": expected [" + expected + "] to be contained in [" + actual + "]",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else if (test != null) {
                test.log(Status.FAIL, failMessage + ": expected [" + expected + "] to be contained in [" + actual + "]");
            }
            
            logger.error(failMessage + ": expected [" + expected + "] to be contained in [" + actual + "]");
            throw e; // Re-throw the assertion error
        }
    }

    /**
     * Assert that a number is less than another number
     * @param actual Actual number
     * @param expected Expected maximum value
     * @param passMessage Message for pass case
     * @param failMessage Message for fail case
     */
    public static void assertLessThan(long actual, long expected, String passMessage, String failMessage) {
        TestContext context = TestContext.getInstance();
        ExtentTest test = context.getExtentTest();
        
        try {
            Assert.assertTrue(actual < expected, failMessage);
            
            // Log pass in report
            if (test != null) {
                test.log(Status.PASS, passMessage);
            }
            logger.info(passMessage);
        } catch (AssertionError e) {
            // Take screenshot on failure
            String screenshotPath = captureScreenshot();
            
            // Log failure with screenshot in report
            if (test != null && screenshotPath != null) {
                test.log(Status.FAIL, failMessage + ": expected [" + actual + "] to be less than [" + expected + "]",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else if (test != null) {
                test.log(Status.FAIL, failMessage + ": expected [" + actual + "] to be less than [" + expected + "]");
            }
            
            logger.error(failMessage + ": expected [" + actual + "] to be less than [" + expected + "]");
            throw e; // Re-throw the assertion error
        }
    }

    /**
     * Capture screenshot for current test
     * @return Path to the captured screenshot or null if failed
     */
    private static String captureScreenshot() {
        TestContext context = TestContext.getInstance();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "screenshot_" + timestamp + ".png";
        String filePath = SCREENSHOT_PATH + File.separator + filename;
        
        try {
            // For web tests using Playwright
            if ("web".equals(context.getPlatform())) {
                Page page = DriverManager.getPage();
                Path path = Paths.get(filePath);
                byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setPath(path));
                logger.info("Captured web screenshot: {}", filePath);
                return filePath;
            } 
            // For mobile tests using Appium
            else if ("mobile".equals(context.getPlatform())) {
                AppiumDriver driver = DriverManager.getMobileDriver();
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Files.copy(screenshotFile.toPath(), Paths.get(filePath));
                logger.info("Captured mobile screenshot: {}", filePath);
                return filePath;
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
        }
        
        return null;
    }
} 