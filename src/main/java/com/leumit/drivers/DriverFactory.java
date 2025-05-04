package com.leumit.drivers;

import com.leumit.config.ConfigManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * DriverFactory - Factory class to create browser and mobile drivers
 */
public class DriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final ConfigManager config = ConfigManager.getInstance();

    /**
     * Create a Playwright browser instance
     * @return Playwright browser instance
     */
    public static Browser createBrowser() {
        String browserName = config.getProperty("browser", "chrome").toLowerCase();
        boolean headless = config.getBooleanProperty("headless", false);
        
        logger.info("Creating {} browser, headless: {}", browserName, headless);
        
        Playwright playwright = Playwright.create();
        
        return switch (browserName) {
            case "firefox" -> playwright.firefox().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless)
            );
            case "webkit" -> playwright.webkit().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless)
            );
            default -> playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setChannel("chrome")
                            .setHeadless(headless)
            );
        };
    }
    
    /**
     * Create a Playwright Page
     * @param browser Playwright browser instance
     * @return Playwright Page
     */
    public static Page createPage(Browser browser) {
        int timeout = config.getIntProperty("timeout", 30) * 1000;
        
        Page page = browser.newPage();
        page.setDefaultTimeout(timeout);
        
        logger.info("Created Playwright page with timeout: {}ms", timeout);
        return page;
    }

    /**
     * Create an Appium driver for Android
     * @return AndroidDriver instance
     */
    public static AndroidDriver createAndroidDriver() {
        try {
            String appiumUrl = config.getProperty("appiumUrl", "http://localhost:4723");
            String deviceName = config.getProperty("deviceNameAndroid", "Galaxy S24 Emulator");
            String appPackage = config.getProperty("androidAppPackage");
            String appActivity = config.getProperty("androidAppActivity");
            
            UiAutomator2Options options = new UiAutomator2Options()
                    .setDeviceName(deviceName)
                    .setAppPackage(appPackage)
                    .setAppActivity(appActivity)
                    .setNewCommandTimeout(Duration.ofSeconds(60));
            
            logger.info("Creating Android driver for device: {}, package: {}", deviceName, appPackage);
            return new AndroidDriver(new URL(appiumUrl), options);
        } catch (Exception e) {
            logger.error("Failed to create Android driver", e);
            throw new RuntimeException("Failed to create Android driver", e);
        }
    }

    /**
     * Create an Appium driver for iOS
     * @return IOSDriver instance
     */
    public static IOSDriver createIOSDriver() {
        try {
            String appiumUrl = config.getProperty("appiumUrl", "http://localhost:4723");
            String deviceName = config.getProperty("deviceNameIOS", "iPhone 14");
            String bundleId = config.getProperty("iosAppBundleId");
            
            XCUITestOptions options = new XCUITestOptions()
                    .setDeviceName(deviceName)
                    .setBundleId(bundleId)
                    .setNewCommandTimeout(Duration.ofSeconds(60));
            
            logger.info("Creating iOS driver for device: {}, bundleId: {}", deviceName, bundleId);
            return new IOSDriver(new URL(appiumUrl), options);
        } catch (Exception e) {
            logger.error("Failed to create iOS driver", e);
            throw new RuntimeException("Failed to create iOS driver", e);
        }
    }

    /**
     * Create an Appium driver based on the platform
     * @param platform Platform name (android/ios)
     * @return AppiumDriver instance
     */
    public static AppiumDriver createMobileDriver(String platform) {
        if ("ios".equalsIgnoreCase(platform)) {
            return createIOSDriver();
        } else {
            return createAndroidDriver();
        }
    }
} 