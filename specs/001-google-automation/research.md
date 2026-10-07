# Research: Google Nifty 50 Search

## Existing Project Findings

- The repository already has `GoogleAutomation.feature` tagged `@google` and a matching
  Java Cucumber step definition.
- The step currently starts browser work in the step-definition constructor, checks a
  numeric value against a hard-coded threshold, and catches exceptions without failing
  the Cucumber step.
- `PageObjectModel.SearchBox` is the existing Google search-field locator, and
  `path.properties` supplies the configured homepage.
- `Selenium.getDriver()` creates the configured browser session but currently calls
  operating-system process termination for Chrome or Edge before creating it.
- `TestNG_TestRunner` is in `src/main/java`, and a focused Surefire invocation did not
  discover it as a test. The repository already has a JUnit 4 Cucumber runner in
  `src/test/java`.

## Decisions

### Verify the results region, not a price

- **Decision**: Search Google for the exact phrase “Nifty 50” and verify matching text
  inside the search-results region without regard to case.
- **Rationale**: This follows the user's selection and avoids a volatile financial
  value or the previous hard-coded numeric threshold.
- **Alternatives considered**: Checking the homepage without a search would not
  exercise the requested search journey. Extracting an index price is outside scope.

### Use the existing browser automation stack

- **Decision**: Keep the test in the current Cucumber feature and Java step-definition
  class, using existing Selenium helpers and no new dependencies. Add a focused JUnit 4
  Cucumber runner in the test source tree.
- **Rationale**: Surefire can select a test-source class by name; the current TestNG
  runner is outside the test source tree and the attempted targeted run found no tests.
- **Alternatives considered**: Changing the broad Surefire/TestNG suite configuration
  would have a wider impact than adding a runner for this feature.

### Do not terminate unrelated browser processes

- **Decision**: Remove the shared driver's `taskkill` calls and let WebDriver manage
  only its own session.
- **Rationale**: A test must not close browser windows or sessions unrelated to itself.
- **Alternatives considered**: Keeping global process termination risks data loss and
  violates the project constitution's safe-default principle.
