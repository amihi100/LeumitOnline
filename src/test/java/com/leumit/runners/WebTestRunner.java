package com.leumit.runners;

import com.leumit.drivers.DriverManager;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.AfterClass;

/**
 * WebTestRunner - Test runner for web Cucumber tests
 */
@CucumberOptions(
    features = "src/test/resources/features/web",
    glue = {"com.leumit.steps", "com.leumit.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/web-report.html",
        "json:target/cucumber-reports/web-report.json",
        "junit:target/cucumber-reports/web-report.xml",
        "timeline:target/cucumber-reports/web-timeline",
        "rerun:target/failed_scenarios.txt"
    },
    monochrome = true,
    tags = "@web"
)
public class WebTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Run scenarios sequentially to share browser per feature
     * @return Scenario data provider
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
    
    /**
     * Clean up all resources after the test class runs
     */
    @AfterClass
    public void cleanUp() {
        DriverManager.closeAllFeatureBrowsers();
    }
} 