# Practice AI Development Toolkit

Practice remains a Java BDD/Selenium project and now includes a local AI-assisted
development API with Java, JavaScript, and Python clients.

## Requirements and current scope

1. **AI API integration:** send prompts through a configurable local Claw API,
   OpenAI-compatible endpoint, or Anthropic Messages endpoint.
2. **Code and text analysis:** submit text or source code with a language label
   and receive the configured provider's response.
3. **Document extraction:** Apache Tika detects and extracts text from common
   text, source, XML, JSON, PDF, Office, and image formats. Scanned PDFs and
   images need Tesseract OCR and the matching language data installed locally.
4. **Cross-language access:** standard-library JavaScript and Python clients
   call the same local HTTP API.
5. **Safe defaults:** the server binds to loopback, upload and extraction sizes
   are capped, and provider keys are read from environment variables rather
   than checked-in configuration.
6. **Repeatable checks:** GitHub Actions runs Java, JavaScript, and Python
   checks on their declared runtime versions.

This is not a promise to read every possible file format. Encrypted, damaged,
unsupported, over-limit, or text-empty files return an error. Image understanding
is OCR-based text extraction; this does not provide vision-model analysis,
image generation, speech recognition, or speech synthesis.

## Integration boundaries

- `jarvisapk`: the provider contracts, environment-backed configuration, bounded
  document extraction, OCR settings, and API error behavior informed this
  integration.
- `agentic-playwright-mcp-framework`: its AI test-planning/API-gateway use cases
  informed the toolkit's direction; its separate workspace and dependencies
  remain independent.
- `demo` and `Portfolio_Krishna`: their Elm/Spring and React/Vite applications
  remain separate because they have independent build and runtime structures.
- `claw-code-main`: no source was copied or reproduced. Its package metadata
  identifies the contents as leaked, unlicensed third-party source. The toolkit
  supports only the documented, user-configured HTTP API contract.

Generated outputs, dependency trees, Git metadata, credentials, and whole sibling
repositories are not copied into this project. Existing project files remain in
place; the additions here are named and self-contained.

## Run the toolkit

Use JDK 25 and Maven 3.9 or later:

```powershell
mvn --settings .mvn/settings.xml spring-boot:run
```

The API listens on `http://127.0.0.1:8080`. Configure the AI provider in
`src/main/resources/application.yaml` or override it through environment
variables:

| Variable | Meaning |
| --- | --- |
| `PRACTICE_API_PROTOCOL` | `claw`, `openai-compatible`, or `anthropic` |
| `PRACTICE_API_ENDPOINT` | Prompt endpoint URL |
| `PRACTICE_API_HEALTH_ENDPOINT` | Provider health-check URL |
| `PRACTICE_API_MODEL` | Model identifier |
| `PRACTICE_API_KEY` | Optional provider token |
| `OPENAI_API_KEY` | Fallback key for OpenAI-compatible endpoints |
| `ANTHROPIC_API_KEY` | Fallback key for Anthropic |
| `PRACTICE_TOOLKIT_URL` | Base URL used by the JavaScript/Python clients |

For compatibility with Jarvis, `JARVIS_API_*` environment variables are also
accepted. API keys are sent only to HTTPS providers or loopback services.
Maven settings contain public repository configuration only; do not put
credentials in `.mvn/settings.xml`.

## HTTP API

| Method and path | Purpose |
| --- | --- |
| `GET /api/health` | Local server health |
| `GET /api/provider-health` | Check the configured provider |
| `GET /api/ask?question=...` | Send a prompt |
| `POST /api/read-text?text=...` | Analyze submitted text |
| `POST /api/analyze-code?language=...&code=...` | Request a code review |
| `POST /api/read-file` | Multipart upload (`file`) plus optional `question` |

Example:

```powershell
Invoke-RestMethod 'http://127.0.0.1:8080/api/ask?question=Create%20a%20test%20plan'
```

## JavaScript and Python clients

Node.js 20 or later:

```powershell
npm run test:toolkit
node tools/javascript/ai-client.mjs "Create a test plan for sign-in"
```

Python 3.10 or later:

```powershell
python -m unittest discover -s tools/python/tests -v
python tools/python/ai_client.py "Create a test plan for sign-in"
```

## Cucumber BDD tests

The toolkit scenarios are defined in `src/test/resources/features/toolkit.feature`,
with step definitions in `src/test/java/stepDefinations/toolkit/ToolkitStepDefinitions.java`.
Their local-only test settings live in `src/test/resources/features/toolkit-bdd.yaml`.
The scenarios start the API on a dynamically assigned loopback port, verify its health
response, extract text from an XML fixture, and confirm oversized files are rejected.
They do not require provider credentials or access to a public website.
JavaScript and Python clients currently use native unit tests rather than Cucumber step
definitions; these tests exercise their HTTP behavior against isolated local mock servers.

The Google automation scenario is in
`src/test/resources/features/GoogleAutomation.feature`. Run it with
`mvn --settings .mvn/settings.xml -Dtest=GoogleAutomationCucumberTest test`. It requires
network access to Google and the browser configured for Selenium. It searches for
“Nifty 50” and checks result text; it does not validate a live market price.

Application defaults are in `src/main/resources/application.yml`; activate the `dev`
Spring profile to use `src/main/resources/application-dev.yml`. Keep API credentials in
environment variables rather than either YAML file.

Run only these scenarios with:

```powershell
mvn --settings .mvn/settings.xml -Dtest=ToolkitCucumberTest test
```

## Validate and package

```powershell
mvn --settings .mvn/settings.xml test
mvn --settings .mvn/settings.xml package
npm run test:toolkit
python -m unittest discover -s tools/python/tests -v
```
