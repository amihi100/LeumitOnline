Feature: Install and Validate App

  @mobile @install
  Scenario: App installation
    Given I check if "leumit.mobile" is installed
    When Not installed, install from Google Play
    Then Assert app is installed on "Galaxy S24 Emulator" 