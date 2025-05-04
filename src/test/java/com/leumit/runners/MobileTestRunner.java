package com.leumit.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * MobileTestRunner - Test runner for mobile Cucumber tests
 */
@CucumberOptions(
    features = "src/test/resources/features/mobile",
    glue = {"com.leumit.steps", "com.leumit.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/mobile-report.html",
        "json:target/cucumber-reports/mobile-report.json",
        "junit:target/cucumber-reports/mobile-report.xml",
        "timeline:target/cucumber-reports/mobile-timeline",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        "rerun:target/failed_scenarios.txt"
    },
    monochrome = true,
    tags = "@mobile"
)
public class MobileTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Run scenarios in parallel
     * @return Scenario data provider
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
} 