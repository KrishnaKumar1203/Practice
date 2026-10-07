package com.example.openai;

import com.example.toolkit.AiProviderClient;
import com.example.toolkit.DocumentReaderService;
import com.example.toolkit.DocumentReaderService.ExtractedDocument;
import com.example.toolkit.ToolkitSettings;
import org.apache.tika.exception.TikaException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@SpringBootApplication
@RequestMapping("/api")
public class OpenAIAPIWebsite {
    private final AiProviderClient aiProviderClient;
    private final DocumentReaderService documentReaderService;

    public OpenAIAPIWebsite() {
        try {
            ToolkitSettings settings = ToolkitSettings.load();
            this.aiProviderClient = new AiProviderClient(settings);
            this.documentReaderService = new DocumentReaderService(settings);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Practice toolkit configuration.", exception);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenAIAPIWebsite.class, args);
    }

    @GetMapping("/ask")
    public CompletableFuture<String> askQuestion(@RequestParam String question) {
        return aiProviderClient.sendQuestionAsync(question);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/provider-health")
    public CompletableFuture<Map<String, Boolean>> providerHealth() {
        return aiProviderClient.checkConnectionAsync()
                .thenApply(connected -> Map.of("connected", connected));
    }

    @PostMapping("/read-text")
    public CompletableFuture<String> readText(@RequestParam String text) {
        return aiProviderClient.sendQuestionAsync("Analyze the following text:\n\n" + text);
    }

    @PostMapping("/analyze-code")
    public CompletableFuture<String> analyzeCode(
            @RequestParam String code,
            @RequestParam String language) {
        if (code.isBlank() || language.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code and language are required.");
        }
        return aiProviderClient.sendQuestionAsync(
                "Review this " + language.strip() + " code for correctness, security, and maintainability. "
                        + "Explain findings and provide a corrected version where useful:\n\n"
                        + code.strip());
    }

    @PostMapping(value = "/read-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<FileAnalysis> readFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "Summarize and analyze this file.") String question)
            throws IOException, TikaException, SAXException {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select a non-empty file.");
        }

        Path temporaryFile = Files.createTempFile("practice-toolkit-", ".upload");
        ExtractedDocument extracted;
        try {
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, temporaryFile, StandardCopyOption.REPLACE_EXISTING);
            }
            extracted = documentReaderService.read(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }

        String prompt = question.strip() + "\n\nFile: " + extracted.fileName()
                + "\nContent type: " + extracted.contentType()
                + "\n\nExtracted content:\n" + extracted.text();
        return aiProviderClient.sendQuestionAsync(prompt)
                .thenApply(answer -> new FileAnalysis(
                        extracted.fileName(), extracted.contentType(), extracted.characters(), answer));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    public record FileAnalysis(String fileName, String contentType, int extractedCharacters, String answer) {
    }
}
