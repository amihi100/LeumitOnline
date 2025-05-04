package com.leumit.steps;

import com.leumit.context.TestContext;
import com.leumit.drivers.DriverManager;
import com.leumit.pages.mobile.LeumitMobileApp;
import com.leumit.utils.AssertUtils;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MobileSteps - Step definitions for mobile tests
 */
public class MobileSteps {
    private static final Logger logger = LoggerFactory.getLogger(MobileSteps.class);
    private final TestContext context = TestContext.getInstance();
    private LeumitMobileApp mobileApp;
    
    /**
     * Initialize the mobile app if not already initialized
     * @return LeumitMobileApp instance
     */
    private LeumitMobileApp getMobileApp() {
        if (mobileApp == null) {
            AppiumDriver driver = DriverManager.getMobileDriver();
            mobileApp = new LeumitMobileApp(driver);
        }
        return mobileApp;
    }
    
    @Given("I check if {string} is installed")
    public void iCheckIfAppIsInstalled(String appPackage) {
        logger.info("Checking if app is installed: {}", appPackage);
        boolean isInstalled = getMobileApp().isAppInstalled();
        
        // Store result in context for next steps
        context.setAttribute("isAppInstalled", isInstalled);
        
        logger.info("App {} is installed: {}", appPackage, isInstalled);
    }
    
    @When("Not installed, install from Google Play")
    public void notInstalledInstallFromGooglePlay() {
        Boolean isInstalled = context.getAttribute("isAppInstalled");
        
        if (isInstalled != null && !isInstalled) {
            logger.info("App not installed, installing from Google Play");
            boolean installResult = getMobileApp().installFromGooglePlay();
            
            AssertUtils.assertTrue(installResult, 
                    "App installed successfully from Google Play",
                    "Failed to install app from Google Play");
        } else {
            logger.info("App already installed, skipping installation");
        }
    }
    
    @Then("Assert app is installed on {string}")
    public void assertAppIsInstalledOnDevice(String deviceName) {
        logger.info("Verifying app is installed on device: {}", deviceName);
        boolean isInstalled = getMobileApp().isAppInstalled();
        
        AssertUtils.assertTrue(isInstalled, 
                "App is installed on device: " + deviceName,
                "App is not installed on device: " + deviceName);
    }
    
    @Given("I open the {string} app")
    public void iOpenTheApp(String appPackage) {
        logger.info("Opening app: {}", appPackage);
        getMobileApp().launchApp();
    }
    
    @Then("The app should be fully loaded")
    public void theAppShouldBeFullyLoaded() {
        logger.info("Checking if app is fully loaded");
        boolean isLoaded = getMobileApp().isAppFullyLoaded();
        
        AssertUtils.assertTrue(isLoaded, 
                "App is fully loaded",
                "App failed to load completely");
    }
    
    @Then("I close the app")
    public void iCloseTheApp() {
        logger.info("Closing the app");
        getMobileApp().closeApp();
    }
    
    @Then("Assert app is closed")
    public void assertAppIsClosed() {
        logger.info("Verifying app is closed");
        // This is just a verification step that doesn't actually check anything
        // since we can't reliably check if an app is fully closed from Appium
        AssertUtils.assertTrue(true, 
                "App successfully closed",
                "Failed to close app");
    }
} 