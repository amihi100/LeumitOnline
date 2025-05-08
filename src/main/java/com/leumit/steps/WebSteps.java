package com.leumit.steps;

import com.leumit.drivers.DriverManager;
import com.leumit.pages.web.LeumitHomePage;
import com.leumit.utils.AssertUtils;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSteps - Step definitions for web tests
 */
public class WebSteps {
    private static final Logger logger = LoggerFactory.getLogger(WebSteps.class);
    private LeumitHomePage homePage;
    
    /**
     * Initialize the home page if not already initialized
     * @return LeumitHomePage instance
     */
    private LeumitHomePage getHomePage() {
        if (homePage == null) {
            Page page = DriverManager.getPage();
            if (page == null) {
                logger.info("Page is null, initializing driver");
                DriverManager.initializeDriver();
                page = DriverManager.getPage();
            }
            homePage = new LeumitHomePage(page);
        }
        return homePage;
    }
    
    @Given("I open the URL {string}")
    public void iOpenTheURL(String url) {
        logger.info("Opening URL: {}", url);
        getHomePage().openHomePage(url);
        
        // Assert the page has been loaded properly
        String pageTitle = getHomePage().getTitle();
        AssertUtils.assertTrue(pageTitle != null && !pageTitle.isEmpty(), 
            "Successfully opened URL: " + url + " with title: '" + pageTitle + "'",
            "Failed to open URL or retrieve page title for: " + url);
    }
    
    @Then("The page title should contain {string}")
    public void thePageTitleShouldContain(String expectedTitle) {
        String actualTitle = getHomePage().getTitle();
        String url = DriverManager.getPage().url();
        logger.info("Actual page title: {}", actualTitle);
        
        boolean containsTitle = actualTitle.contains(expectedTitle);
        
        AssertUtils.assertTrue(containsTitle, 
            "Page title contains expected text: '" + expectedTitle + "' in '" + actualTitle + "'",
            "Page title does not contain expected text: '" + expectedTitle + "' in '" + actualTitle + "'");
    }
    
    @Then("The logo at {string} should be visible")
    public void theLogoShouldBeVisible(String selector) {
        boolean isVisible = getHomePage().isElementVisible(selector);
        String url = DriverManager.getPage().url();
        
        logger.info("Checking if logo is visible at: {}", selector);
        
        AssertUtils.assertTrue(isVisible, 
            "Logo is visible at selector: '" + selector + "'",
            "Logo is not visible at selector: '" + selector + "'");
    }
    
    @Then("The identification field {string} should be visible")
    public void theIdentificationFieldShouldBeVisible(String selector) {
        logger.info("Checking if identification field is visible at: {}", selector);
        boolean isVisible = getHomePage().isIdentificationFieldVisible();
        
        AssertUtils.assertTrue(isVisible, 
            "Identification field is visible at selector: '" + selector + "'",
            "Identification field is not visible at selector: '" + selector + "'");
    }
    
    @And("The password field {string} should be visible")
    public void thePasswordFieldShouldBeVisible(String selector) {
        logger.info("Checking if password field is visible at: {}", selector);
        boolean isVisible = getHomePage().isPasswordFieldVisible();
        
        AssertUtils.assertTrue(isVisible, 
            "Password field is visible at selector: '" + selector + "'",
            "Password field is not visible at selector: '" + selector + "'");
    }
    
    @And("The page should load in less than {string} milliseconds")
    public void thePageShouldLoadInLessThanMilliseconds(String maxTimeString) {
        long maxTime = Long.parseLong(maxTimeString);
        long actualTime = getHomePage().getPageLoadTime();
        logger.info("Page load time: {} ms", actualTime);
        
        boolean isLoadTimeAcceptable = actualTime < maxTime;
        
        AssertUtils.assertTrue(isLoadTimeAcceptable, 
            "Page loaded in acceptable time: " + actualTime + " ms (limit: " + maxTime + " ms)",
            "Page load time exceeded limit: " + actualTime + " ms (limit: " + maxTime + " ms)");
    }
    
    @And("I close the browser")
    public void iCloseTheBrowser() {
        logger.info("Closing the browser");
        DriverManager.closeBrowser();
        
        // Assert browser was closed properly
        AssertUtils.assertTrue(DriverManager.getPage() == null, 
            "Browser was closed successfully",
            "Failed to close browser properly");
    }
    
    @Then("Assert browser is closed")
    public void assertBrowserIsClosed() {
        logger.info("Verifying browser is closed");
        AssertUtils.assertTrue(DriverManager.getPage() == null, 
            "Browser is confirmed to be closed",
            "Browser is still open when it should be closed");
    }
} 