package com.leumit.pages.web;

import com.aventstack.extentreports.ExtentTest;
import com.leumit.context.TestContext;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all web page objects
 */
public abstract class BasePageWeb {
    protected static final Logger logger = LoggerFactory.getLogger(BasePageWeb.class);
    protected final Page page;
    protected final TestContext context;
    protected final ExtentTest reporter;

    /**
     * Constructor for BasePageWeb
     * @param page Playwright Page object
     */
    public BasePageWeb(Page page) {
        this.page = page;
        this.context = TestContext.getInstance();
        this.reporter = context.getExtentTest();
    }

    /**
     * Navigate to a URL
     * @param url URL to navigate to
     * @return This page object
     */
    public BasePageWeb navigate(String url) {
        logger.info("Navigating to URL: {}", url);
        page.navigate(url);
        return this;
    }

    /**
     * Get the page title
     * @return Page title
     */
    public String getTitle() {
        return page.title();
    }

    /**
     * Check if element is visible
     * @param selector CSS or XPath selector
     * @return True if element is visible
     */
    public boolean isElementVisible(String selector) {
        try {
            return page.isVisible(selector);
        } catch (Exception e) {
            logger.error("Error checking element visibility: {}", selector, e);
            return false;
        }
    }

    /**
     * Click on an element
     * @param selector CSS or XPath selector
     * @return This page object
     */
    public BasePageWeb click(String selector) {
        logger.info("Clicking element: {}", selector);
        page.click(selector);
        return this;
    }

    /**
     * Fill a form field
     * @param selector CSS or XPath selector
     * @param text Text to fill
     * @return This page object
     */
    public BasePageWeb fill(String selector, String text) {
        logger.info("Filling element: {} with text: {}", selector, text);
        page.fill(selector, text);
        return this;
    }

    /**
     * Get text from an element
     * @param selector CSS or XPath selector
     * @return Element text
     */
    public String getText(String selector) {
        return page.textContent(selector);
    }

    /**
     * Wait for element to be visible
     * @param selector CSS or XPath selector
     * @return This page object
     */
    public BasePageWeb waitForElement(String selector) {
        page.waitForSelector(selector);
        return this;
    }

    /**
     * Measure page load time
     * @return Page load time in milliseconds
     */
    public long measurePageLoadTime() {
        Object loadTime = page.evaluate("() => {" +
                "const timing = window.performance.timing;" +
                "return timing.loadEventEnd - timing.navigationStart;" +
                "}");
        return loadTime instanceof Number ? ((Number) loadTime).longValue() : 0;
    }
} 