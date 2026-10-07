<!--
Sync Impact Report
Version change: no prior constitution -> 1.0.0 (initial ratification)
Modified principles: none; initial principles established
Added sections: Project Constraints; Development Workflow and Quality Gates
Removed sections: none
Templates requiring updates:
  - .specify/templates/plan-template.md: reviewed; no update required
  - .specify/templates/spec-template.md: reviewed; no update required
  - .specify/templates/tasks-template.md: reviewed; no update required
Follow-up TODOs: none
-->

# Practice Constitution

## Core Principles

### I. Preserve Established Contracts
Features MUST preserve documented API routes, request and response formats, error
behavior, and JavaScript/Python client compatibility unless an accepted specification
explicitly changes them. Contract changes MUST include migration and compatibility
considerations. This protects existing callers across the project's language clients.

### II. Secure Defaults and Bounded Processing
The local API MUST bind to loopback by default. Provider credentials MUST come from
environment variables and MUST NOT be committed to source or configuration files.
Provider keys MUST only be sent to HTTPS providers or loopback services. File uploads
and extraction MUST remain bounded; unsupported, encrypted, damaged, empty, or
over-limit inputs MUST return explicit errors rather than success-shaped fallbacks.

### III. Verify Observable Behavior
Every behavior change MUST have acceptance criteria and automated tests appropriate to
its affected surface. Java changes MUST use the existing Maven/JUnit/Cucumber tests as
applicable; JavaScript changes MUST use the Node.js test runner; Python changes MUST
use the standard-library unittest suite. Cross-language or API contract changes MUST
test the affected integration boundaries, including failure and limit cases.

### IV. Keep Project Boundaries and Dependencies Explicit
Changes MUST stay within this project's modules and preserve the independent build and
runtime boundaries of its Java application and JavaScript/Python clients. Do not copy
source from sibling repositories or unlicensed third-party projects. New dependencies
MUST be justified in the plan and use the appropriate existing package manager.

### V. Prefer Small, Documented Changes
Implement only behavior required by the accepted specification, reusing existing
structures before introducing new abstractions. User-visible behavior, configuration,
or supported-format changes MUST be reflected in the relevant project documentation.
Do not claim support for behavior or file formats that the implementation and tests do
not verify.

## Project Constraints

- The primary application is Java 25, built with Maven 3.9 or later and Spring Boot.
- The JavaScript client requires Node.js 20 or later; its tests use `node --test`.
- The Python client requires Python 3.10 or later; its tests use `unittest`.
- Keep provider credentials out of checked-in files. Maven settings contain public
  repository configuration only.
- Existing HTTP endpoints, local-only defaults, configured extraction limits, and
  environment-variable compatibility are part of the current product contract.

## Development Workflow and Quality Gates

- A feature MUST be described by an approved specification before planning
  implementation. The plan MUST identify affected modules, contracts, risks, and
  verification commands; tasks MUST be small, ordered, and traceable to requirements.
- Before implementation, resolve any conflict between the specification and this
  constitution with the project owner; do not silently relax a security or compatibility
  constraint.
- Run the focused tests for each changed surface. For changes crossing language or API
  boundaries, run the relevant integration tests as well. Report any unrun checks and
  their reason.
- Update documentation when a change affects setup, supported behavior, configuration,
  or validation instructions.

## Governance

This constitution governs project specifications, plans, tasks, implementation, and
reviews. Amendments require a documented rationale and review by the project owner.
Versioning follows semantic versioning: MAJOR for backward-incompatible governance
changes, MINOR for new or materially expanded principles, and PATCH for clarifications
that do not change obligations. Each plan and review MUST check compliance; any
exception MUST be recorded with its rationale and approval before implementation.

**Version**: 1.0.0 | **Ratified**: 2026-10-07 | **Last Amended**: 2026-10-07
