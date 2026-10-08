package com.example.toolkit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;

public record ToolkitSettings(
        URI apiEndpoint,
        URI healthEndpoint,
        ApiProtocol apiProtocol,
        String model,
        String apiKey,
        Duration apiTimeout,
        long maxDocumentBytes,
        int maxExtractedCharacters,
        boolean ocrEnabled,
        String ocrLanguage) {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    public static ToolkitSettings load() throws IOException {
        JsonNode config;
        try (InputStream input = ToolkitSettings.class.getResourceAsStream("/application.yml")) {
            if (input == null) {
                throw new IOException("Missing application configuration: /application.yml");
            }
            config = YAML_MAPPER.readTree(input);
        }

        JsonNode api = config.path("api");
        JsonNode documents = config.path("documents");
        JsonNode ocr = documents.path("ocr");
        try {
            ApiProtocol protocol = ApiProtocol.parse(environmentOrDefault(
                    "PRACTICE_API_PROTOCOL",
                    environmentOrDefault("JARVIS_API_PROTOCOL", api.path("protocol").asText("claw"))));
            long timeoutSeconds = api.path("timeout-seconds").asLong(180);
            long maxBytes = documents.path("max-bytes").asLong(33_554_432);
            int maxCharacters = documents.path("max-extracted-characters").asInt(200_000);
            if (timeoutSeconds <= 0 || maxBytes <= 0 || maxCharacters <= 0) {
                throw new IllegalArgumentException("Timeout and document limits must be positive.");
            }

            return new ToolkitSettings(
                    requireHttpUri(environmentOrDefault(
                            "PRACTICE_API_ENDPOINT",
                            environmentOrDefault("JARVIS_API_ENDPOINT",
                                    api.path("endpoint").asText("http://127.0.0.1:5000/api/prompt"))),
                            "api.endpoint"),
                    requireHttpUri(environmentOrDefault(
                            "PRACTICE_API_HEALTH_ENDPOINT",
                            environmentOrDefault("JARVIS_API_HEALTH_ENDPOINT",
                                    api.path("health-endpoint").asText("http://127.0.0.1:5000/api/status"))),
                            "api.health-endpoint"),
                    protocol,
                    environmentOrDefault("PRACTICE_API_MODEL",
                            environmentOrDefault("JARVIS_API_MODEL", api.path("model").asText("local-gemini"))),
                    configuredApiKey(protocol),
                    Duration.ofSeconds(timeoutSeconds),
                    maxBytes,
                    maxCharacters,
                    ocr.path("enabled").asBoolean(true),
                    ocr.path("language").asText("eng"));
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid Practice toolkit configuration.", exception);
        }
    }

    private static URI requireHttpUri(String value, String setting) {
        URI uri = URI.create(value);
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null) {
            throw new IllegalArgumentException(setting + " must be an absolute HTTP(S) URL.");
        }
        return uri;
    }

    private static String environmentOrDefault(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static String firstEnvironment(String... names) {
        for (String name : names) {
            String value = System.getenv(name);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private static String configuredApiKey(ApiProtocol protocol) {
        String key = firstEnvironment("PRACTICE_API_KEY", "JARVIS_API_KEY");
        if (key != null) {
            return key;
        }
        return switch (protocol) {
            case ANTHROPIC -> firstEnvironment("ANTHROPIC_API_KEY");
            case OPENAI_COMPATIBLE -> firstEnvironment("OPENAI_API_KEY");
            case CLAW -> null;
        };
    }

    public enum ApiProtocol {
        CLAW,
        OPENAI_COMPATIBLE,
        ANTHROPIC;

        private static ApiProtocol parse(String value) {
            return switch (value.toLowerCase()) {
                case "claw" -> CLAW;
                case "openai-compatible" -> OPENAI_COMPATIBLE;
                case "anthropic" -> ANTHROPIC;
                default -> throw new IllegalArgumentException(
                        "api.protocol must be claw, openai-compatible, or anthropic.");
            };
        }
    }
}
