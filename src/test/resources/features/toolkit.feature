@toolkit
Feature: AI toolkit API and document parsing
  The local toolkit exposes a health endpoint and extracts text from uploaded documents.

  Background:
    Given the toolkit BDD profile is loaded

  Scenario: Start the API with a local-only test server and check health
    When I start the local API and request its health endpoint
    Then the health endpoint responds with HTTP 200 and status "ok"

  Scenario: Extract text from an XML document
    When I extract the configured XML fixture
    Then the extracted document contains "Practice toolkit BDD fixture"

  Scenario: Reject a document larger than the configured limit
    When I attempt to read a file larger than the configured document limit
    Then the document is rejected with a size-limit error
