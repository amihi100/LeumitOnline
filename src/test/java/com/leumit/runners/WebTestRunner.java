package com.leumit.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

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
    tags = "@web",
    name = "WebTest"
)
public class WebTestRunner extends AbstractTestNGCucumberTests {
    
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