package com.example.toolkit;

import com.example.toolkit.ToolkitSettings.ApiProtocol;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiProviderClientTest {
    @Test
    void usesClawPromptContractAndReadsOutput() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        HttpServer server = server("/api/prompt", "{\"output\":\"Claw answer\"}", requestBody, null);
        try {
            String answer = new AiProviderClient(settings(
                    server, "/api/prompt", ApiProtocol.CLAW, null))
                    .sendQuestionAsync("hello")
                    .get(5, TimeUnit.SECONDS);

            assertEquals("Claw answer", answer);
            assertTrue(requestBody.get().contains("\"prompt\":\"hello\""));
            assertTrue(requestBody.get().contains("\"model\":\"test-model\""));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void supportsOpenAiCompatibleChatCompletionsAndBearerAuth() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> authorization = new AtomicReference<>();
        HttpServer server = server(
                "/v1/chat/completions",
                "{\"choices\":[{\"message\":{\"content\":\"OpenAI answer\"}}]}",
                requestBody,
                authorization);
        try {
            String answer = new AiProviderClient(settings(
                    server, "/v1/chat/completions", ApiProtocol.OPENAI_COMPATIBLE, "test-token"))
                    .sendQuestionAsync("hello")
                    .get(5, TimeUnit.SECONDS);

            assertEquals("OpenAI answer", answer);
            assertEquals("Bearer test-token", authorization.get());
            assertTrue(requestBody.get().contains("\"messages\""));
        } finally {
            server.stop(0);
        }
    }

    private HttpServer server(
            String path,
            String responseBody,
            AtomicReference<String> requestBody,
            AtomicReference<String> authorization) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(path, exchange -> {
            try (exchange) {
                requestBody.set(new String(exchange.getRequestBody().readAllBytes()));
                if (authorization != null) {
                    authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
                }
                byte[] body = responseBody.getBytes();
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            }
        });
        server.start();
        return server;
    }

    private ToolkitSettings settings(
            HttpServer server,
            String path,
            ApiProtocol protocol,
            String apiKey) {
        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        return new ToolkitSettings(
                URI.create(baseUrl + path),
                URI.create(baseUrl + "/health"),
                protocol,
                "test-model",
                apiKey,
                Duration.ofSeconds(5),
                1_000_000,
                10_000,
                false,
                "eng");
    }
}
