package com.leumit.pages.mobile;

import com.aventstack.extentreports.ExtentTest;
import com.leumit.context.TestContext;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Base class for all mobile page objects
 */
public abstract class BasePageMobile {
    protected static final Logger logger = LoggerFactory.getLogger(BasePageMobile.class);
    protected final AppiumDriver driver;
    protected final TestContext context;
    protected final ExtentTest reporter;
    protected final WebDriverWait wait;
    protected final boolean isAndroid;

    /**
     * Constructor for BasePageMobile
     * @param driver AppiumDriver instance
     */
    public BasePageMobile(AppiumDriver driver) {
        this.driver = driver;
        this.context = TestContext.getInstance();
        this.reporter = context.getExtentTest();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.isAndroid = driver instanceof AndroidDriver;
        
        // Initialize elements with AppiumFieldDecorator
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    /**
     * Check if an element is visible
     * @param by Element locator
     * @return True if element is visible
     */
    public boolean isElementVisible(By by) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(by)) != null;
        } catch (Exception e) {
            logger.error("Element not visible: {}", by, e);
            return false;
        }
    }

    /**
     * Click on an element
     * @param by Element locator
     * @return This page object
     */
    public BasePageMobile click(By by) {
        try {
            logger.info("Clicking element: {}", by);
            wait.until(ExpectedConditions.elementToBeClickable(by)).click();
        } catch (Exception e) {
            logger.error("Failed to click element: {}", by, e);
        }
        return this;
    }

    /**
     * Enter text in an element
     * @param by Element locator
     * @param text Text to enter
     * @return This page object
     */
    public BasePageMobile sendKeys(By by, String text) {
        try {
            logger.info("Sending text to element: {}", by);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            logger.error("Failed to send keys to element: {}", by, e);
        }
        return this;
    }

    /**
     * Get text from an element
     * @param by Element locator
     * @return Element text
     */
    public String getText(By by) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getText();
        } catch (Exception e) {
            logger.error("Failed to get text from element: {}", by, e);
            return "";
        }
    }

    /**
     * Check if app is installed
     * @param appPackage Application package name
     * @return True if app is installed
     */
    public boolean isAppInstalled(String appPackage) {
        try {
            if (isAndroid) {
                return ((AndroidDriver) driver).isAppInstalled(appPackage);
            } else {
                return ((IOSDriver) driver).isAppInstalled(appPackage);
            }
        } catch (Exception e) {
            logger.error("Failed to check if app is installed: {}", appPackage, e);
            return false;
        }
    }

    /**
     * Launch app by package name
     * @param appPackage Application package name
     */
    public void launchApp(String appPackage) {
        try {
            if (isAndroid) {
                ((AndroidDriver) driver).activateApp(appPackage);
            } else {
                ((IOSDriver) driver).activateApp(appPackage);
            }
            logger.info("Launched app: {}", appPackage);
        } catch (Exception e) {
            logger.error("Failed to launch app: {}", appPackage, e);
        }
    }

    /**
     * Close the app
     */
    public void closeApp() {
        try {
            String packageName = isAndroid ? 
                    ((AndroidDriver) driver).getCurrentPackage() : 
                    context.getAttribute("currentAppBundleId");
            
            if (isAndroid) {
                ((AndroidDriver) driver).terminateApp(packageName);
            } else {
                ((IOSDriver) driver).terminateApp(packageName);
            }
            logger.info("Closed app: {}", packageName);
        } catch (Exception e) {
            logger.error("Failed to close app", e);
        }
    }
} 