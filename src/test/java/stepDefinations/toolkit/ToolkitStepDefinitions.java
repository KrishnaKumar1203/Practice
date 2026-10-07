package stepDefinations.toolkit;

import com.example.openai.OpenAIAPIWebsite;
import com.example.toolkit.DocumentReaderService;
import com.example.toolkit.ToolkitSettings;
import com.example.toolkit.ToolkitSettings.ApiProtocol;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.tika.exception.TikaException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ToolkitStepDefinitions {
    private static final String PROFILE_RESOURCE = "/features/toolkit-bdd.yaml";
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private JsonNode profile;
    private ToolkitSettings settings;
    private ConfigurableApplicationContext application;
    private int healthStatus;
    private String healthBody;
    private DocumentReaderService.ExtractedDocument extractedDocument;
    private IOException documentReadFailure;
    private Path temporaryFile;

    @Given("the toolkit BDD profile is loaded")
    public void loadToolkitBddProfile() throws IOException {
        try (InputStream input = ToolkitStepDefinitions.class.getResourceAsStream(PROFILE_RESOURCE)) {
            if (input == null) {
                throw new IOException("Missing BDD test configuration: " + PROFILE_RESOURCE);
            }
            profile = YAML_MAPPER.readTree(input);
        }

        URI baseUri = URI.create(requiredText(profile.path("api"), "base-url"));
        String healthPath = requiredText(profile.path("api"), "health-path");
        if (!"http".equalsIgnoreCase(baseUri.getScheme())
                || baseUri.getHost() == null
                || !baseUri.getHost().equals("127.0.0.1")
                || !healthPath.startsWith("/")) {
            throw new IOException("The BDD API profile must use an absolute loopback HTTP URL and an absolute health path.");
        }

        long maxBytes = profile.path("documents").path("max-bytes").asLong(0);
        int maxCharacters = profile.path("documents").path("max-extracted-characters").asInt(0);
        long startupTimeout = profile.path("api").path("startup-timeout-seconds").asLong(0);
        if (maxBytes <= 0 || maxCharacters <= 0 || startupTimeout <= 0) {
            throw new IOException("BDD document limits and API startup timeout must be positive.");
        }

        settings = new ToolkitSettings(
                baseUri.resolve("/api/prompt"),
                baseUri.resolve(healthPath),
                ApiProtocol.CLAW,
                "bdd-test",
                null,
                Duration.ofSeconds(startupTimeout),
                maxBytes,
                maxCharacters,
                profile.path("documents").path("ocr-enabled").asBoolean(false),
                profile.path("documents").path("ocr-language").asText("eng"));
    }

    @When("I start the local API and request its health endpoint")
    public void startApiAndRequestHealth() throws Exception {
        URI baseUri = URI.create(requiredText(profile.path("api"), "base-url"));
        application = new SpringApplicationBuilder(OpenAIAPIWebsite.class)
                .web(WebApplicationType.SERVLET)
                .run(
                        "--server.address=" + baseUri.getHost(),
                        "--server.port=0",
                        "--spring.profiles.active=dev",
                        "--spring.main.banner-mode=off");

        String localPort = application.getEnvironment().getProperty("local.server.port");
        if (localPort == null) {
            throw new IllegalStateException("The toolkit did not expose its dynamically assigned server port.");
        }

        URI healthUri = new URI(
                baseUri.getScheme(),
                null,
                baseUri.getHost(),
                Integer.parseInt(localPort),
                requiredText(profile.path("api"), "health-path"),
                null,
                null);
        HttpURLConnection connection = (HttpURLConnection) healthUri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(Math.toIntExact(settings.apiTimeout().toMillis()));
        connection.setReadTimeout(Math.toIntExact(settings.apiTimeout().toMillis()));
        try {
            healthStatus = connection.getResponseCode();
            try (InputStream response = healthStatus < 400
                    ? connection.getInputStream()
                    : connection.getErrorStream()) {
                if (response == null) {
                    throw new IOException("The health endpoint returned no response body.");
                }
                healthBody = new String(response.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }
        } finally {
            connection.disconnect();
        }
    }

    @Then("the health endpoint responds with HTTP 200 and status {string}")
    public void verifyHealthResponse(String expectedStatus) throws IOException {
        assertTrue(healthStatus > 0, "The health endpoint was not requested.");
        assertEquals(200, healthStatus);
        JsonNode response = new ObjectMapper().readTree(healthBody);
        assertEquals(expectedStatus, response.path("status").asText());
    }

    @When("I extract the configured XML fixture")
    public void extractConfiguredXmlFixture() throws IOException, TikaException, SAXException {
        String fixture = requiredText(profile.path("documents"), "fixture");
        try (InputStream input = ToolkitStepDefinitions.class.getResourceAsStream(fixture)) {
            if (input == null) {
                throw new IOException("Missing XML fixture resource: " + fixture);
            }
            temporaryFile = Files.createTempFile("practice-toolkit-bdd-", ".xml");
            Files.copy(input, temporaryFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        extractedDocument = new DocumentReaderService(settings).read(temporaryFile);
    }

    @Then("the extracted document contains {string}")
    public void verifyExtractedContent(String expectedContent) {
        assertNotNull(extractedDocument, "The XML fixture was not extracted.");
        assertTrue(
                extractedDocument.text().contains(expectedContent),
                () -> "Extracted document did not contain expected text: " + expectedContent);
    }

    @When("I attempt to read a file larger than the configured document limit")
    public void readOversizedDocument() throws IOException, TikaException, SAXException {
        temporaryFile = Files.createTempFile("practice-toolkit-bdd-oversized-", ".txt");
        long maxBytes = settings.maxDocumentBytes();
        try (var output = Files.newOutputStream(temporaryFile)) {
            byte[] block = new byte[8192];
            long remaining = maxBytes + 1;
            while (remaining > 0) {
                int length = (int) Math.min(block.length, remaining);
                output.write(block, 0, length);
                remaining -= length;
            }
        }

        try {
            new DocumentReaderService(settings).read(temporaryFile);
        } catch (IOException exception) {
            documentReadFailure = exception;
        }
    }

    @Then("the document is rejected with a size-limit error")
    public void verifyOversizedDocumentWasRejected() {
        assertNotNull(documentReadFailure, "The oversized document was not rejected.");
        assertTrue(
                documentReadFailure.getMessage().toLowerCase(Locale.ROOT).contains("configured limit"),
                () -> "Unexpected document rejection: " + documentReadFailure.getMessage());
    }

    @After
    public void cleanUpScenarioResources() throws Exception {
        if (application != null) {
            application.close();
            application = null;
        }
        if (temporaryFile != null) {
            Files.deleteIfExists(temporaryFile);
            temporaryFile = null;
        }
    }

    private static String requiredText(JsonNode parent, String key) throws IOException {
        JsonNode value = parent.path(key);
        if (!value.isTextual() || value.asText().isBlank()) {
            throw new IOException("Missing required BDD configuration value: " + key);
        }
        return value.asText().trim();
    }
}
