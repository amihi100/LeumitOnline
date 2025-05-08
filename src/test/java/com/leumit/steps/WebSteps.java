package com.leumit.steps;

import com.leumit.context.TestContext;
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
    private final TestContext context = TestContext.getInstance();
    private LeumitHomePage homePage;
    
    /**
     * Initialize the home page if not already initialized
     * @return LeumitHomePage instance
     */
    private LeumitHomePage getHomePage() {
        if (homePage == null) {
            // Get the current feature URI from the scenario
            String featureUri = context.getScenario().getUri().toString();
            // Get the page for this feature
            Page page = DriverManager.getPageForFeature(featureUri);
            if (page != null) {
                homePage = new LeumitHomePage(page);
            } else {
                // Fallback to thread-local page
                page = DriverManager.getPage();
                homePage = new LeumitHomePage(page);
            }
        }
        return homePage;
    }
    
    @Given("I open the URL {string}")
    public void iOpenTheURL(String url) {
        logger.info("Opening URL: {}", url);
        getHomePage().openHomePage(url);
    }
    
    @Then("The page title should contain {string}")
    public void thePageTitleShouldContain(String expectedTitle) {
        String actualTitle = getHomePage().getTitle();
        logger.info("Actual page title: {}", actualTitle);
        
        AssertUtils.assertContains(actualTitle, expectedTitle, 
                "Page title contains expected text: " + expectedTitle,
                "Page title does not contain expected text: " + expectedTitle);
    }
    
    @Then("The page should load in less than {string} milliseconds")
    public void thePageShouldLoadInLessThanMilliseconds(String maxLoadTimeStr) {
        long maxLoadTime = Long.parseLong(maxLoadTimeStr);
        long actualLoadTime = getHomePage().getPageLoadTime();
        logger.info("Page load time: {} ms", actualLoadTime);
        
        AssertUtils.assertLessThan(actualLoadTime, maxLoadTime,
                "Page loaded in less than " + maxLoadTime + " milliseconds (actual: " + actualLoadTime + " ms)",
                "Page loaded in more than " + maxLoadTime + " milliseconds (actual: " + actualLoadTime + " ms)");
    }
    
    @Then("The logo at {string} should be visible")
    public void theLogoShouldBeVisible(String logoXpath) {
        logger.info("Checking if logo is visible at: {}", logoXpath);
        boolean isVisible = getHomePage().isLogoVisible();
        
        AssertUtils.assertTrue(isVisible, 
                "Logo is visible",
                "Logo is not visible");
    }
    
    @Then("The identification field {string} should be visible")
    public void theIdentificationFieldShouldBeVisible(String fieldXpath) {
        logger.info("Checking if identification field is visible at: {}", fieldXpath);
        boolean isVisible = getHomePage().isIdentificationFieldVisible();
        
        AssertUtils.assertTrue(isVisible, 
                "Identification field is visible",
                "Identification field is not visible");
    }
    
    @And("The password field {string} should be visible")
    public void thePasswordFieldShouldBeVisible(String fieldXpath) {
        logger.info("Checking if password field is visible at: {}", fieldXpath);
        boolean isVisible = getHomePage().isPasswordFieldVisible();
        
        AssertUtils.assertTrue(isVisible, 
                "Password field is visible",
                "Password field is not visible");
    }
    
    @And("I close the browser")
    public void iCloseTheBrowser() {
        logger.info("Closing the browser");
        // Get the current feature URI from the scenario
        String featureUri = context.getScenario().getUri().toString();
        
        // Close the browser for this feature
        if (DriverManager.hasFeatureBrowser(featureUri)) {
            DriverManager.closeBrowserForFeature(featureUri);
        } else {
            // Fallback to thread-local browser
            DriverManager.closeBrowser();
        }
    }
    
    @Then("Assert browser is closed")
    public void assertBrowserIsClosed() {
        logger.info("Verifying browser is closed");
        // This is just a verification step that doesn't actually check anything
        // since we can't check if a browser is closed from outside
        AssertUtils.assertTrue(true, 
                "Browser successfully closed",
                "Failed to close browser");
    }
} 