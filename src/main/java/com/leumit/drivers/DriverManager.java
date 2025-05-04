package com.leumit.drivers;

import com.leumit.context.TestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DriverManager - Manages browser and mobile drivers with thread safety
 */
public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriver> mobileDriverThreadLocal = new ThreadLocal<>();

    /**
     * Get or create a new Playwright Page for web testing
     * @return Playwright Page
     */
    public static synchronized Page getPage() {
        if (pageThreadLocal.get() == null) {
            Browser browser = getBrowser();
            Page page = DriverFactory.createPage(browser);
            pageThreadLocal.set(page);
            logger.info("Created new Playwright Page for thread: {}", Thread.currentThread().getId());
        }
        return pageThreadLocal.get();
    }

    /**
     * Get or create a new Browser for web testing
     * @return Playwright Browser
     */
    public static synchronized Browser getBrowser() {
        if (browserThreadLocal.get() == null) {
            Playwright playwright = getPlaywright();
            Browser browser = DriverFactory.createBrowser();
            browserThreadLocal.set(browser);
            logger.info("Created new Browser for thread: {}", Thread.currentThread().getId());
        }
        return browserThreadLocal.get();
    }

    /**
     * Get or create a new Playwright instance
     * @return Playwright instance
     */
    public static synchronized Playwright getPlaywright() {
        if (playwrightThreadLocal.get() == null) {
            Playwright playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);
            logger.info("Created new Playwright instance for thread: {}", Thread.currentThread().getId());
        }
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
} 