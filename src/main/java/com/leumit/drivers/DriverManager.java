package com.leumit.drivers;

import com.leumit.context.TestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DriverManager - Manages browser and mobile drivers with thread safety
 */
public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriver> mobileDriverThreadLocal = new ThreadLocal<>();
    private static final ConcurrentHashMap<Long, Boolean> initializedDrivers = new ConcurrentHashMap<>();
    
    // Store browsers for each feature
    private static final Map<String, Browser> featureBrowsers = new ConcurrentHashMap<>();
    private static final Map<String, Page> featurePages = new ConcurrentHashMap<>();
    private static final Map<String, Playwright> featurePlaywrights = new ConcurrentHashMap<>();

    /**
     * Initialize the driver for the current thread
     */
    public static void initializeDriver() {
        long threadId = Thread.currentThread().getId();
        if (!initializedDrivers.containsKey(threadId)) {
            logger.info("Created new Playwright instance for thread: {}", threadId);
            playwrightThreadLocal.set(Playwright.create());
            browserThreadLocal.set(DriverFactory.createBrowser());
            pageThreadLocal.set(DriverFactory.createPage(browserThreadLocal.get()));
            initializedDrivers.put(threadId, true);
        }
    }
    
    /**
     * Initialize the driver for a specific feature
     * @param featureUri The feature URI to initialize a driver for
     * @return true if a new browser was initialized, false if one already existed
     */
    public static synchronized boolean initializeDriverForFeature(String featureUri) {
        if (!featureBrowsers.containsKey(featureUri)) {
            Playwright playwright = Playwright.create();
            featurePlaywrights.put(featureUri, playwright);
            
            Browser browser = DriverFactory.createBrowser(playwright);
            featureBrowsers.put(featureUri, browser);
            
            Page page = DriverFactory.createPage(browser);
            featurePages.put(featureUri, page);
            
            logger.info("Initialized new browser for feature: {}", featureUri);
            return true;
        }
        logger.info("Reusing existing browser for feature: {}", featureUri);
        return false;
    }
    
    /**
     * Get the page for a specific feature
     * @param featureUri The feature URI
     * @return The Page instance for the feature
     */
    public static synchronized Page getPageForFeature(String featureUri) {
        return featurePages.get(featureUri);
    }
    
    /**
     * Get the browser for a specific feature
     * @param featureUri The feature URI
     * @return The Browser instance for the feature 
     */
    public static synchronized Browser getBrowserForFeature(String featureUri) {
        return featureBrowsers.get(featureUri);
    }
    
    /**
     * Check if the feature has a browser initialized
     * @param featureUri The feature URI
     * @return true if the feature has a browser initialized
     */
    public static synchronized boolean hasFeatureBrowser(String featureUri) {
        return featureBrowsers.containsKey(featureUri);
    }

    /**
     * Get or create a new Playwright Page for web testing
     * @return Playwright Page
     */
    public static synchronized Page getPage() {
        return pageThreadLocal.get();
    }

    /**
     * Get or create a new Browser for web testing
     * @return Playwright Browser
     */
    public static synchronized Browser getBrowser() {
        return browserThreadLocal.get();
    }

    /**
     * Get or create a new Playwright instance
     * @return Playwright instance
     */
    public static synchronized Playwright getPlaywright() {
        return playwrightThreadLocal.get();
    }

    /**
     * Get or create a new AppiumDriver for mobile testing
     * @return AppiumDriver instance
     */
    public static synchronized AppiumDriver getMobileDriver() {
        if (mobileDriverThreadLocal.get() == null) {
            String platform = TestContext.getInstance().getPlatform();
            AppiumDriver driver = DriverFactory.createMobileDriver(platform);
            mobileDriverThreadLocal.set(driver);
            logger.info("Created new Mobile Driver for platform: {} and thread: {}", 
                    platform, Thread.currentThread().getId());
        }
        return mobileDriverThreadLocal.get();
    }

    /**
     * Closes and quits the current browser for web testing
     */
    public static synchronized void closeBrowser() {
        Page page = pageThreadLocal.get();
        if (page != null) {
            try {
                page.close();
                logger.info("Closed Page for thread: {}", Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error("Error closing Page", e);
            } finally {
                pageThreadLocal.remove();
            }
        }

        Browser browser = browserThreadLocal.get();
        if (browser != null) {
            try {
                browser.close();
                logger.info("Closed Browser for thread: {}", Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error("Error closing Browser", e);
            } finally {
                browserThreadLocal.remove();
            }
        }

        long threadId = Thread.currentThread().getId();
        initializedDrivers.remove(threadId);
    }
    
    /**
     * Close the browser for a specific feature
     * @param featureUri The feature URI
     */
    public static synchronized void closeBrowserForFeature(String featureUri) {
        Page page = featurePages.get(featureUri);
        if (page != null) {
            try {
                page.close();
                logger.info("Closed Page for feature: {}", featureUri);
            } catch (Exception e) {
                logger.error("Error closing Page for feature: {}", featureUri, e);
            } finally {
                featurePages.remove(featureUri);
            }
        }

        Browser browser = featureBrowsers.get(featureUri);
        if (browser != null) {
            try {
                browser.close();
                logger.info("Closed Browser for feature: {}", featureUri);
            } catch (Exception e) {
                logger.error("Error closing Browser for feature: {}", featureUri, e);
            } finally {
                featureBrowsers.remove(featureUri);
            }
        }
        
        Playwright playwright = featurePlaywrights.get(featureUri);
        if (playwright != null) {
            try {
                playwright.close();
                logger.info("Closed Playwright for feature: {}", featureUri);
            } catch (Exception e) {
                logger.error("Error closing Playwright for feature: {}", featureUri, e);
            } finally {
                featurePlaywrights.remove(featureUri);
            }
        }
    }

    /**
     * Closes Playwright instance completely
     */
    public static synchronized void closePlaywright() {
        Playwright playwright = playwrightThreadLocal.get();
        if (playwright != null) {
            try {
                playwright.close();
                logger.info("Closed Playwright for thread: {}", Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error("Error closing Playwright", e);
            } finally {
                playwrightThreadLocal.remove();
            }
        }
    }

    /**
     * Closes and quits the mobile driver
     */
    public static synchronized void closeMobileDriver() {
        AppiumDriver driver = mobileDriverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Closed Mobile Driver for thread: {}", Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error("Error closing Mobile Driver", e);
            } finally {
                mobileDriverThreadLocal.remove();
            }
        }
    }

    /**
     * Closes all drivers for the current thread
     */
    public static synchronized void closeAllDrivers() {
        closeBrowser();
        closeMobileDriver();
        closePlaywright();
        logger.info("Closed all drivers for thread: {}", Thread.currentThread().getId());
    }
    
    /**
     * Close all feature browsers
     */
    public static synchronized void closeAllFeatureBrowsers() {
        for (String featureUri : featureBrowsers.keySet()) {
            closeBrowserForFeature(featureUri);
        }
        featureBrowsers.clear();
        featurePages.clear();
        featurePlaywrights.clear();
        logger.info("Closed all feature browsers");
    }
} 