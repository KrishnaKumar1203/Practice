package com.example.toolkit;

import com.example.toolkit.ToolkitSettings.ApiProtocol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentReaderServiceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void extractsTextAndMetadataFromPlainFiles() throws Exception {
        Path file = temporaryDirectory.resolve("notes.txt");
        Files.writeString(file, "JavaScript, Python, and Java toolkit.");

        var document = new DocumentReaderService(settings(1_000)).read(file);

        assertEquals("notes.txt", document.fileName());
        assertTrue(document.contentType().startsWith("text/plain"));
        assertTrue(document.text().contains("JavaScript, Python, and Java"));
    }

    @Test
    void rejectsFilesAboveTheConfiguredLimit() throws IOException {
        Path file = temporaryDirectory.resolve("large.txt");
        Files.writeString(file, "file exceeds limit");

        IOException exception = assertThrows(
                IOException.class,
                () -> new DocumentReaderService(settings(2)).read(file));

        assertTrue(exception.getMessage().contains("configured limit"));
    }

    private ToolkitSettings settings(long maxBytes) {
        return new ToolkitSettings(
                URI.create("http://127.0.0.1:5000/api/prompt"),
                URI.create("http://127.0.0.1:5000/api/status"),
                ApiProtocol.CLAW,
                "test-model",
                null,
                Duration.ofSeconds(5),
                maxBytes,
                10_000,
                false,
                "eng");
    }
}
