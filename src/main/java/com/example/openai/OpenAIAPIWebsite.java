package com.example.openai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@RestController
@SpringBootApplication
@RequestMapping("/api")
public class OpenAIAPIWebsite {

    public static void main(String[] args) {
        SpringApplication.run(OpenAIAPIWebsite.class, args);
    }

    @GetMapping("/ask")
    public String askQuestion(@RequestParam String question) {
        // Call OpenAI API to get response
        return "AI Response for: " + question;
    }

    @PostMapping("/generate-image")
    public String generateImage(@RequestParam String prompt) {
        // Call OpenAI API to generate an image
        return "Generated Image URL for: " + prompt;
    }

    @PostMapping("/voice-assistance")
    public String voiceAssistance(@RequestParam String voiceInput) {
        // Convert voice to text and process
        return "Processed voice input: " + voiceInput;
    }

    @PostMapping("/read-text")
    public String readText(@RequestParam String text) {
        // Process text and provide response
        return "Processed Text: " + text;
    }

    @PostMapping("/analyze-code")
    public String analyzeCode(@RequestParam String code, @RequestParam String language) {
        // Analyze and optimize code
        return "Optimized Code in " + language + " for: " + code;
    }

    @PostMapping("/extract-text-from-voice")
    public String extractTextFromVoice(@RequestParam String audioInput) {
        // Convert voice to text
        return "Extracted text from voice input: " + audioInput;
    }
}

