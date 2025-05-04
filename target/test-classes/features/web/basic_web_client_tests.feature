Feature: Basic Web Client UI Checks

  @web @ui
  Scenario: Navigate to home page
    Given I open the URL "https://leumit.co.il/"
    Then The page title should contain "לאומית"

  @web @ui
  Scenario: Logo should be visible
    Given I open the URL "https://leumit.co.il/"
    Then The logo at "img" should be visible

  @web @ui
  Scenario: Login fields should be visible
    Given I open the URL "https://www.leumit.co.il/"
    Then The identification field "input[name='IdNumTextBox']" should be visible
    And The password field "input[name='PasswordTextBox']" should be visible 