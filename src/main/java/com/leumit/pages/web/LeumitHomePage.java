package com.leumit.pages.web;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LeumitHomePage - Page object for Leumit home page
 */
public class LeumitHomePage extends BasePageWeb {
    private static final Logger logger = LoggerFactory.getLogger(LeumitHomePage.class);
    
    // Page elements with name attribute selectors
    private static final String LOGO_SELECTOR = "img"; // Any image on the page
    private static final String IDENTIFICATION_FIELD = "input[name='IdNumTextBox']"; // Name selector for identification
    private static final String PASSWORD_FIELD = "input[name='PasswordTextBox']"; // Name selector for password
    
    /**
     * Constructor for LeumitHomePage
     * @param page Playwright Page object
     */
    public LeumitHomePage(Page page) {
        super(page);
    }
    
    /**
     * Open Leumit home page
     * @param url URL to navigate to
     * @return This page object
     */
    public LeumitHomePage openHomePage(String url) {
        logger.info("Opening Leumit home page: {}", url);
        navigate(url);
        return this;
    }
    
    /**
     * Check if logo is visible
     * @return True if logo is visible
     */
    public boolean isLogoVisible() {
        logger.info("Checking if logo is visible");
        return isElementVisible(LOGO_SELECTOR);
    }
    
    /**
     * Check if identification field is visible
     * @return True if identification field is visible
     */
    public boolean isIdentificationFieldVisible() {
        logger.info("Checking if identification field is visible");
        
        // Try to detect and switch to iframe if needed
        if (getPage().frames().size() > 1) {
            logger.info("Multiple frames detected, attempting to search in all frames");
            for (com.microsoft.playwright.Frame frame : getPage().frames()) {
                try {
                    if (frame.isVisible(IDENTIFICATION_FIELD)) {
                        logger.info("Found identification field in frame: {}", frame.name());
                        return true;
                    }
                } catch (Exception e) {
                    logger.debug("Failed to check frame: {}", frame.name(), e);
                }
            }
        }
        
        return isElementVisible(IDENTIFICATION_FIELD);
    }
    
    /**
     * Check if password field is visible
     * @return True if password field is visible
     */
    public boolean isPasswordFieldVisible() {
        logger.info("Checking if password field is visible");
        
        // Try to detect and switch to iframe if needed
        if (getPage().frames().size() > 1) {
            logger.info("Multiple frames detected, attempting to search in all frames");
            for (com.microsoft.playwright.Frame frame : getPage().frames()) {
                try {
                    if (frame.isVisible(PASSWORD_FIELD)) {
                        logger.info("Found password field in frame: {}", frame.name());
                        return true;
                    }
                } catch (Exception e) {
                    logger.debug("Failed to check frame: {}", frame.name(), e);
                }
            }
        }
        
        return isElementVisible(PASSWORD_FIELD);
    }
    
    /**
     * Get page load time in milliseconds
     * @return Page load time in milliseconds
     */
    public long getPageLoadTime() {
        logger.info("Measuring page load time");
        return measurePageLoadTime();
    }
} 