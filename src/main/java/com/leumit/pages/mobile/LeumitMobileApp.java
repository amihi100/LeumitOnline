package com.leumit.pages.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LeumitMobileApp - Page object for Leumit mobile app
 */
public class LeumitMobileApp extends BasePageMobile {
    private static final Logger logger = LoggerFactory.getLogger(LeumitMobileApp.class);
    
    // App package
    private static final String APP_PACKAGE = "leumit.mobile";
    
    // Common locators (Using By for flexibility)
    private static final By SPLASH_SCREEN = By.id("splash_image");
    private static final By HOME_SCREEN = By.id("home_container");
    
    // Last measured load time
    private long lastLoadTime = 0;
    
    // Page elements using PageFactory
    @AndroidFindBy(id = "splash_image")
    @iOSXCUITFindBy(accessibility = "splash_image")
    private WebElement splashImage;
    
    @AndroidFindBy(id = "home_container")
    @iOSXCUITFindBy(accessibility = "home_container")
    private WebElement homeContainer;
    
    /**
     * Constructor for LeumitMobileApp
     * @param driver AppiumDriver instance
     */
    public LeumitMobileApp(AppiumDriver driver) {
        super(driver);
    }
    
    /**
     * Get the app package name
     * @return App package name
     */
    public String getAppPackage() {
        return APP_PACKAGE;
    }
    
    /**
     * Get the last measured app load time in milliseconds
     * @return App load time in milliseconds
     */
    public long getLastLoadTime() {
        return lastLoadTime;
    }
    
    /**
     * Check if app is installed
     * @return True if app is installed
     */
    public boolean isAppInstalled() {
        logger.info("Checking if Leumit app is installed");
        return isAppInstalled(APP_PACKAGE);
    }
    
    /**
     * Launch the app
     * @return This page object
     */
    public LeumitMobileApp launchApp() {
        logger.info("Launching Leumit app");
        launchApp(APP_PACKAGE);
        return this;
    }
    
    /**
     * Check if app is fully loaded
     * @return True if app is fully loaded
     */
    public boolean isAppFullyLoaded() {
        logger.info("Checking if app is fully loaded");
        try {
            long startTime = System.currentTimeMillis();
            
            // First wait for splash screen
            wait.until(ExpectedConditions.visibilityOfElementLocated(SPLASH_SCREEN));
            // Then wait for home screen
            wait.until(ExpectedConditions.visibilityOfElementLocated(HOME_SCREEN));
            
            lastLoadTime = System.currentTimeMillis() - startTime;
            logger.info("App fully loaded in {} ms", lastLoadTime);
            
            return true;
        } catch (Exception e) {
            logger.error("App failed to load completely", e);
            return false;
        }
    }
    
    /**
     * Close the app
     */
    public void closeApp() {
        logger.info("Closing Leumit app");
        super.closeApp();
    }
    
    /**
     * Install app from Google Play (mock implementation)
     * @return True if installation successful
     */
    public boolean installFromGooglePlay() {
        logger.info("Installing Leumit app from Google Play (mock)");
        // This would be a mock implementation since actual installation requires deeper integration
        // with adb or Play Store API
        return true;
    }
} 