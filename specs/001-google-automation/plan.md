# Implementation Plan: Google Nifty 50 Search

**Branch**: `not-created` | **Date**: 2026-10-07 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `specs/001-google-automation/spec.md`

## Summary

Refactor the existing Google automation scenario into explicit browser steps that
search for “Nifty 50” and assert the results content contains the phrase. Make browser
cleanup reliable and remove the shared driver helper's behavior of killing every
Chrome or Edge process before creating its own session.

## Technical Context

**Language/Version**: Java 25

**Primary Dependencies**: Selenium 4.12.1, Cucumber 7.18.0, TestNG

**Storage**: None

**Testing**: Maven Surefire with a focused JUnit 4 Cucumber runner

**Target Platform**: Windows development environment; Google over the network

**Project Type**: Maven application with browser-automation tests

**Performance Goals**: Search field and matching result content each wait no longer
than 20 seconds

**Constraints**: Reuse the configured Google URL and existing browser selection.
The test needs network access and can be blocked by external consent or anti-bot pages.
Do not add dependencies or retrieve a live index value.

## Constitution Check

- Preserve established contracts: PASS. No API or client contract changes.
- Secure defaults and bounded processing: PASS. No credentials are introduced; browser
  teardown only closes the WebDriver session created by this scenario.
- Verify observable behavior: PASS. The Gherkin scenario asserts matching result text;
  navigation, missing content, and timeouts fail the scenario.
- Keep project boundaries and dependencies explicit: PASS. Changes remain within the
  existing Java/Selenium/Cucumber test surface and add no dependency.
- Prefer small, documented changes: PASS. Reuse current project helpers and document
  the external-browser/network prerequisites.

## Project Structure

### Documentation (this feature)

```text
specs/001-google-automation/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── checklists/
│   └── requirements.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/
├── utility/
│   ├── PageObjectModel.java
│   └── Selenium.java
└── com/example/ConfigReader.java

src/test/
├── java/stepDefinations/Java/GoogleAutomation.java
├── java/org/myProject/GoogleAutomationCucumberTest.java
└── resources/features/GoogleAutomation.feature

README.md
```

**Structure Decision**: Keep the scenario in the existing Cucumber feature and
step-definition class. Add a focused JUnit 4 runner under `src/test/java` because the
existing TestNG runner is under `src/main/java` and the targeted Surefire invocation
did not discover it. Extend the existing Google page-object selectors and Selenium
helper; do not add another browser framework.

## Phase 0: Research

See [research.md](research.md). The project already has a tagged Google Cucumber
scenario, a configured URL, a Selenium driver factory, and a search-field locator.
No new technology choice or external API is needed.

## Phase 1: Design

- Scenario flow: open configured homepage → wait for search field → submit “Nifty 50”
  → wait for the results region to contain the phrase → assert it case-insensitively.
- Use the existing Google search-field locator and add a locator for the results
  region. Do not inspect a numeric market quote or use a fixed price threshold.
- Keep driver creation in the existing Selenium factory. Remove its process-wide
  `taskkill` calls so starting this WebDriver session cannot terminate unrelated
  user browser sessions.
- Put browser cleanup in a Cucumber `@After` hook and quit only the driver owned by
  the step-definition instance. Do not catch and suppress navigation, wait, assertion,
  or cleanup errors.
- No persistent data model or API contracts are involved. See [data-model.md](data-model.md).
- Update the README's BDD test description with the external network/browser
  prerequisite and the scenario's verified behavior.

## Implementation Tasks

1. **T001** Update the Gherkin scenario to express opening Google, searching for the
   phrase, and checking the results.
2. **T002** Add a focused JUnit/Cucumber test runner for the Google feature.
3. **T003** Add the Google results-region locator to the existing page-object utility.
4. **T004** Remove process-wide browser termination from the Selenium driver factory.
5. **T005** Implement explicit search/assertion steps and per-scenario driver cleanup.
6. **T006** Document the scenario and its external prerequisites in the README.
7. **T007** Run the focused Maven test and report any external-service limitation.

## Requirement Mapping

| Requirement | Implementation tasks |
|-------------|---------------------|
| FR-001 Open the configured Google homepage | T001, T005 |
| FR-002 Submit the “Nifty 50” query | T001, T005 |
| FR-003 Check result-region text case-insensitively | T001, T003, T005 |
| FR-004 Fail explicitly on missing results or timeout | T001, T002, T005 |
| FR-005 Close the scenario's browser session | T005 |
| FR-006 Do not terminate unrelated browser processes | T004 |

## Verification

Run the focused Google scenario:

```powershell
mvn --settings .mvn/settings.xml -Dtest=GoogleAutomationCucumberTest test
```

The Google scenario requires network access and a working configured browser/driver.
If the external service blocks automation, report that as an environment limitation;
do not weaken the assertion to make the test pass.
