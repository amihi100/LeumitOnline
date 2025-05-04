# LeumitOnline Automation Framework

A professional cross-platform (Windows and Mac) Java automation framework for web and mobile testing of the Leumit Healthcare Services application.

## 🧱 Technology Stack

- Java 19+
- Maven
- TestNG
- Playwright Java (for Web testing)
- Appium Java client (for Mobile testing)
- ExtentReports (for reporting)
- Cucumber (for BDD)
- AssertUtils (custom assertions)
- Log4j2/SLF4J for logging

## 📂 Project Structure

The project follows OOP and SOLID principles with the following structure:

```
src/
├── main/
│   └── java/com/leumit/
│       ├── context/
│       │   └── TestContext.java
│       ├── drivers/
│       │   ├── DriverFactory.java
│       │   └── DriverManager.java
│       ├── pages/
│       │   ├── web/
│       │   └── mobile/
│       ├── utils/
│       │   └── AssertUtils.java
│       └── config/
│           └── ConfigManager.java
├── test/
│   └── java/com/leumit/
│       ├── hooks/
│       │   └── TestHooks.java
│       ├── runners/
│       │   ├── WebTestRunner.java
│       │   └── MobileTestRunner.java
│       └── steps/
│           ├── WebSteps.java
│           └── MobileSteps.java
└── resources/
    ├── features/
    │   ├── web/
    │   └── mobile/
    └── config.properties
```

## 🔍 Key Components

- **TestContext**: Thread-safe singleton using ThreadLocal for test data
- **DriverFactory**: Creates Playwright or Appium driver based on platform/test
- **DriverManager**: Uses ThreadLocal, synchronized with TestContext, handles cleanup
- **Page Objects**: For both Web and Mobile testing
- **AssertUtils**: Custom assertion utilities with ExtentReports integration

## 🧪 Test Execution

### Running Tests

To build the project without running tests:
```
mvn clean install -DskipTests
```

To run all tests:
```
mvn clean test
```

To run tests and generate detailed reports:
```
mvn clean verify
```

To run only web tests:
```
mvn clean test -Dcucumber.filter.tags="@web"
```

To run only mobile tests:
```
mvn clean test -Dcucumber.filter.tags="@mobile"
```

To run specific test categories:
```
# Performance tests only
mvn clean test -Dcucumber.filter.tags="@performance"

# UI tests only
mvn clean test -Dcucumber.filter.tags="@ui"
```

To run test combinations:
```
# Web performance tests
mvn clean test -Dcucumber.filter.tags="@web and @performance"

# Web UI tests
mvn clean test -Dcucumber.filter.tags="@web and @ui" 

# Mobile install tests
mvn clean test -Dcucumber.filter.tags="@mobile and @install"
```

To run with specific configuration:
```
# Run with Firefox browser
mvn clean test -Dcucumber.filter.tags="@web" -Dbrowser=firefox

# Run in headless mode
mvn clean test -Dcucumber.filter.tags="@web" -Dheadless=true
```

To run specific test runners:
```
# Run web tests only using WebTestRunner
mvn clean test -Dtest=WebTestRunner

# Run mobile tests only using MobileTestRunner
mvn clean test -Dtest=MobileTestRunner
```

### Test Reports

After test execution, reports are generated in:
- Cucumber HTML Reports: `target/cucumber-reports/html/`
- ExtentReports: `target/extent-reports/`
- Screenshots (on failures): `target/screenshots/`

## 🔧 Configuration

Configuration can be modified in:
```
src/test/resources/config/config.properties
```

You can also override configuration via command line:
```
mvn test -Dbrowser=firefox -Dheadless=true
```

## 🧰 Thread Safety

All shared resources (drivers, test data, ExtentReports) use ThreadLocal to ensure thread safety for parallel test execution.

## 📊 Reporting Features

- Parent/child ExtentTests
- Screenshot capturing on failure
- Performance metrics
- Multi-platform reporting 