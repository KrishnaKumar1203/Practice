# Quickstart: Google Nifty 50 Search

## Prerequisites

- JDK 25 and Maven 3.9 or later, as required by the project.
- A supported browser and matching WebDriver available to the configured Selenium
  driver factory.
- Network access to Google. Consent prompts, CAPTCHA pages, or service outages can
  prevent this external end-to-end scenario from completing.

## Run

From the repository root:

```powershell
mvn --settings .mvn/settings.xml -Dtest=GoogleAutomationCucumberTest test
```

The focused Cucumber `@google` scenario searches for “Nifty 50” and passes only if the
results region contains that phrase, ignoring case. It does not validate a market
price.
