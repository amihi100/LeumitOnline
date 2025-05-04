Feature: Launch and Validate App UI

  @mobile @launch
  Scenario: App launch validation
    Given I open the "leumit.mobile" app
    Then The app should be fully loaded
    And I close the app
    Then Assert app is closed 