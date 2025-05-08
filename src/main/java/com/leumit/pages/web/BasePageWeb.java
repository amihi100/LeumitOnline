package com.leumit.pages.web;

import com.leumit.drivers.DriverManager;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasePageWeb - Base class for web page objects
 */
public class BasePageWeb {
    private static final Logger logger = LoggerFactory.getLogger(BasePageWeb.class);
    protected Page page;
    
    /**
     * Constructor for BasePageWeb
     * @param page Playwright Page object
     */
    public BasePageWeb(Page page) {
        this.page = page;
    }
    
    /**
     * Get the current page or initialize it if null
     * @return Playwright Page object
     */
    protected Page getPage() {
        if (page == null) {
            page = DriverManager.getPage();
        }
        return page;
    }
    
    /**
     * Navigate to a URL
     * @param url URL to navigate to
     */
    public void navigate(String url) {
        logger.info("Navigating to URL: {}", url);
        getPage().navigate(url);
    }
    
    /**
     * Check if an element is visible
     * @param selector CSS selector for the element
     * @return True if the element is visible
     */
    public boolean isElementVisible(String selector) {
        try {
            return getPage().isVisible(selector);
        } catch (Exception e) {
            logger.error("Error checking if element is visible: {}", selector, e);
            return false;
        }
    }
    
    /**
     * Check if an element exists
     * @param selector CSS selector for the element
     * @return True if the element exists
     */
    public boolean elementExists(String selector) {
        try {
            return getPage().querySelector(selector) != null;
        } catch (Exception e) {
            logger.error("Error checking if element exists: {}", selector, e);
            return false;
        }
    }
    
    /**
     * Click on an element
     * @param selector CSS selector for the element
     */
    public void click(String selector) {
        logger.info("Clicking on element: {}", selector);
        getPage().click(selector);
    }
    
    /**
     * Type text into an element
     * @param selector CSS selector for the element
     * @param text Text to type
     */
    public void type(String selector, String text) {
        logger.info("Typing text into element: {}", selector);
        getPage().fill(selector, text);
    }
    
    /**
     * Get text from an element
     * @param selector CSS selector for the element
     * @return Text content of the element
     */
    public String getText(String selector) {
        return getPage().textContent(selector);
    }
    
    /**
     * Get the page title
     * @return Page title
     */
    public String getTitle() {
        return getPage().title();
    }
    
    /**
     * Measure page load time in milliseconds
     * @return Page load time in milliseconds
     */
    public long measurePageLoadTime() {
        long startTime = System.currentTimeMillis();
        getPage().waitForLoadState();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
} 