# Leumit Online Test Automation Framework

This project contains automated tests for the Leumit website using Playwright, Cucumber, and TestNG.

## Prerequisites

- Java 19 or higher
- Maven 3.8.0 or higher
- Chrome browser installed

## Project Structure

```
src/
├── main/java/com/leumit/
│   ├── config/         # Configuration management
│   ├── drivers/        # WebDriver and Playwright setup
│   ├── pages/          # Page Object Models
│   ├── steps/          # Step definitions
│   └── utils/          # Utility classes
└── test/
    ├── java/com/leumit/
    │   ├── hooks/      # Cucumber hooks
    │   └── runners/    # Test runners
    └── resources/
        ├── config/     # Configuration files
        └── features/   # Cucumber feature files
```

## Running Tests

### Basic Test Execution

To run all tests:
```bash
mvn clean test
```

### Running Specific Test Types

1. Web Tests:
```bash
mvn clean test -Dtest=WebTestRunner
```

2. API Tests:
```bash
mvn clean test -Dtest=ApiTestRunner
```

### Running Tests with Tags

The framework supports several tags for test categorization:

- `@web` - Web UI tests
- `@api` - API tests
- `@ui` - UI-specific tests
- `@performance` - Performance tests
- `@smoke` - Smoke tests

Examples:

1. Run all web tests except UI tests:
```bash
mvn clean test -Dtest=WebTestRunner -Dcucumber.filter.tags="@web and not @ui"
```

2. Run only performance tests:
```bash
mvn clean test -Dtest=WebTestRunner -Dcucumber.filter.tags="@performance"
```

3. Run smoke tests for both web and API:
```bash
mvn clean test -Dtest=WebTestRunner -Dcucumber.filter.tags="@smoke"
```

### Test Reports

After test execution, reports are generated in:
- Extent Reports: `target/extent-reports/`
- Cucumber Reports: `target/cucumber-reports/`

## Configuration

The framework uses a properties file for configuration:
- Location: `src/test/resources/config/config.properties`
- Contains settings for:
  - Browser type
  - Headless mode
  - Timeouts
  - URLs
  - API endpoints

## Browser Management

- Each **scenario** runs in its own browser instance
- A new browser is created at the start of each scenario
- The browser is automatically closed after each scenario completes
- This ensures complete isolation between scenarios
- Parallel execution is supported with each scenario having its own browser
- Default browser is Chrome (non-headless)

## Logging

- Logs are written to `logs/` directory
- Log level can be configured in `log4j2.xml`
- Each test run creates a new log file with timestamp

## Best Practices

1. **Tag Usage**:
   - Use `@web` for all web tests
   - Use `@api` for all API tests
   - Use `@ui` for UI-specific tests
   - Use `@performance` for performance tests
   - Use `@smoke` for smoke tests

2. **Test Organization**:
   - Keep feature files focused and small
   - Use meaningful scenario names
   - Follow the Given-When-Then format
   - Reuse step definitions when possible

3. **Page Objects**:
   - Keep page objects clean and focused
   - Use meaningful method names
   - Handle waits and timeouts appropriately
   - Use the base page for common functionality

## Troubleshooting

1. **Browser Issues**:
   - Ensure Chrome is installed and up to date
   - Check browser driver versions match
   - Verify headless mode settings

2. **Test Failures**:
   - Check the test reports for detailed failure information
   - Verify the application is accessible
   - Check network connectivity
   - Review logs for additional details

## Contributing

1. Create a new branch for your changes
2. Follow the existing code style
3. Add appropriate tests
4. Update documentation as needed
5. Submit a pull request

## License

This project is proprietary and confidential. 