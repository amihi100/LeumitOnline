Feature: Basic Web Server Tests

  @web @performance
  Scenario: Performance testing
    Given I open the URL "https://leumit.co.il/"
    Then The page title should contain "לאומית"
    And The page should load in less than "10000" milliseconds

  @web
  Scenario: Page title verification
    Given I open the URL "https://leumit.co.il/"
    Then The page title should contain "לאומית"
    And I close the browser
    Then Assert browser is closed 