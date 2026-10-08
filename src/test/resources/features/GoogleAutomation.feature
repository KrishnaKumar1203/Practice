Feature: Google Automation

@google
Scenario: Open Google homepage and check Nifty 50
  Given I open the Google homepage
  When I search Google for "Nifty 50"
  Then the Google results mention "Nifty 50"