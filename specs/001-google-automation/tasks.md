# Tasks: Google Nifty 50 Search

**Input**: Design documents from `specs/001-google-automation/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: The Gherkin scenario is the requested acceptance test and will be updated
before its implementation.

**Organization**: Tasks are grouped by the single P1 user story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Parallelizable work in distinct files with no incomplete dependencies
- **[Story]**: The user story the task serves
- File paths are repository-relative.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

Spec Kit and the existing Maven/Cucumber project are initialized; no additional setup
task is required.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Blocking shared prerequisites

No new shared infrastructure or dependencies are required for this feature.

---

## Phase 3: User Story 1 - Verify a Google search for Nifty 50 (Priority: P1) 🎯 MVP

**Goal**: Search the configured Google homepage for “Nifty 50” and fail unless the
results content mentions that phrase.

**Independent Test**: Run the configured Maven suite with network and browser access;
the tagged Cucumber scenario passes only when the results region contains the phrase.

### Tests for User Story 1

- [x] T001 [US1] Update `src/test/resources/features/GoogleAutomation.feature` with
  separate Given, When, and Then steps for opening Google, searching “Nifty 50”, and
  checking the results content.
- [x] T002 [P] [US1] Add a focused JUnit/Cucumber runner for
  `src/test/resources/features/GoogleAutomation.feature` in
  `src/test/java/org/myProject/GoogleAutomationCucumberTest.java`.

### Implementation for User Story 1

- [x] T003 [P] [US1] Add a Google results-region locator to
  `src/main/java/utility/PageObjectModel.java`.
- [x] T004 [P] [US1] Remove Chrome/Edge process-wide termination from
  `src/main/java/utility/Selenium.java`; leave WebDriver session lifecycle to Selenium.
- [x] T005 [US1] Refactor `src/test/java/stepDefinations/Java/GoogleAutomation.java`
  into explicit Cucumber steps, wait up to 20 seconds for matching results, assert
  “Nifty 50” case-insensitively, propagate failures, and quit only its own driver in
  an `@After` hook.
- [x] T006 [P] [US1] Document the Google scenario, browser/network prerequisites, and
  result-text-only scope in `README.md`.

**Checkpoint**: The scenario searches for the phrase, checks results rather than the
search input or a market-price threshold, and closes only its own browser session.

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Verify the complete feature and its project-specific quickstart

- [x] T007 Run `mvn --settings .mvn/settings.xml -Dtest=GoogleAutomationCucumberTest test`
  and record whether the Google scenario is blocked by browser, network, or Google
  interstitial behavior.
  **Result**: Compilation and Cucumber dry-run passed. Live execution was blocked
  before browser startup because Selenium Manager could not resolve the EdgeDriver
  download host; the current network environment has no matching cached EdgeDriver.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Already complete.
- **Foundational (Phase 2)**: No tasks are needed.
- **User Story 1 (Phase 3)**: T001 establishes the acceptance scenario; T002, T003,
  T004, and T006 touch separate files and can proceed independently; T005 depends on
  T001, T003, and T004.
- **Polish (Phase 4)**: T007 depends on all implementation tasks.

### User Story Dependencies

- **User Story 1 (P1)**: No other user stories or new infrastructure.

### Parallel Opportunities

- After T001 is written, T002, T003, T004, and T006 touch distinct files and can
  proceed in parallel. T005 follows the feature and helper tasks. T007 is final
  validation.

## Parallel Example: User Story 1

```text
T002: Add the JUnit/Cucumber runner in src/test/java/org/myProject/GoogleAutomationCucumberTest.java
T003: Add the results locator in src/main/java/utility/PageObjectModel.java
T004: Remove global process termination in src/main/java/utility/Selenium.java
T006: Update README.md with the scenario and prerequisites
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Update the Gherkin test and confirm the desired steps are represented.
2. Update the page locator and safe driver lifecycle behavior.
3. Implement the steps and assertion.
4. Update documentation and run the focused Maven test.

### Incremental Delivery

There is one user story. Validate it independently through the existing tagged Cucumber
scenario; no additional story phases apply.

## Notes

- [P] tasks touch different files and have no dependencies on incomplete tasks.
- The test depends on public Google availability; external blocking must be reported,
  not hidden by weakening the assertion.
- Live browser assertions remain unverified until a matching EdgeDriver is available
  and the environment can reach Google.
- No market price, financial threshold, or new external data service is in scope.
