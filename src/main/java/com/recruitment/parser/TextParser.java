package com.recruitment.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Plain text file parser
 */
public class TextParser {
    
    /**
     * Extract text content from plain text file
     */
    public String extractText(File textFile) throws IOException {
        if (textFile == null || !textFile.exists()) {
            throw new IllegalArgumentException("Text file does not exist");
        }
        
        if (!textFile.getName().toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("File is not a text file");
        }
        
        try {
            Path filePath = textFile.toPath();
            
            // Check file size (limit to reasonable size for resume)
            long fileSize = Files.size(filePath);
            if (fileSize > 10 * 1024 * 1024) { // 10MB limit
                throw new IOException("Text file is too large (max 10MB)");
            }
            
            if (fileSize == 0) {
                throw new IOException("Text file is empty");
            }
            
            // Read file content with UTF-8 encoding
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            
            if (content.trim().isEmpty()) {
                throw new IOException("Text file contains no readable content");
            }
            
            return cleanExtractedText(content);
            
        } catch (IOException e) {
            // Try with different encoding if UTF-8 fails
            try {
                String content = Files.readString(textFile.toPath(), StandardCharsets.ISO_8859_1);
                return cleanExtractedText(content);
            } catch (IOException e2) {
                throw new IOException("Failed to read text file: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Extract text with specific encoding
     */
    public String extractText(File textFile, String encoding) throws IOException {
        if (textFile == null || !textFile.exists()) {
            throw new IllegalArgumentException("Text file does not exist");
        }
        
        if (encoding == null || encoding.trim().isEmpty()) {
            throw new IllegalArgumentException("Encoding cannot be null or empty");
        }
        
        try {
            Path filePath = textFile.toPath();
            
            // Check file size
            long fileSize = Files.size(filePath);
            if (fileSize > 10 * 1024 * 1024) { // 10MB limit
                throw new IOException("Text file is too large (max 10MB)");
            }
            
            if (fileSize == 0) {
                throw new IOException("Text file is empty");
            }
            
            // Read with specified encoding
            String content = Files.readString(filePath, java.nio.charset.Charset.forName(encoding));
            
            if (content.trim().isEmpty()) {
                throw new IOException("Text file contains no readable content");
            }
            
            return cleanExtractedText(content);
            
        } catch (IOException e) {
            throw new IOException("Failed to read text file with encoding " + encoding + ": " + e.getMessage(), e);
        } catch (java.nio.charset.UnsupportedCharsetException e) {
            throw new IllegalArgumentException("Unsupported encoding: " + encoding, e);
        }
    }
    
    /**
     * Detect file encoding (basic detection)
     */
    public String detectEncoding(File textFile) throws IOException {
        if (textFile == null || !textFile.exists()) {
            throw new IllegalArgumentException("Text file does not exist");
        }
        
        try {
            byte[] bytes = Files.readAllBytes(textFile.toPath());
            
            if (bytes.length == 0) {
                return StandardCharsets.UTF_8.name();
            }
            
            // Check for BOM (Byte Order Mark)
            if (bytes.length >= 3 && 
                bytes[0] == (byte) 0xEF && 
                bytes[1] == (byte) 0xBB && 
                bytes[2] == (byte) 0xBF) {
                return StandardCharsets.UTF_8.name();
            }
            
            if (bytes.length >= 2) {
                if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) {
                    return StandardCharsets.UTF_16LE.name();
                }
                if (bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) {
                    return StandardCharsets.UTF_16BE.name();
                }
            }
            
            // Simple heuristic: try to decode as UTF-8
            try {
                new String(bytes, StandardCharsets.UTF_8);
                return StandardCharsets.UTF_8.name();
            } catch (Exception e) {
                // If UTF-8 fails, assume ISO-8859-1 (Latin-1)
                return StandardCharsets.ISO_8859_1.name();
            }
            
        } catch (IOException e) {
            throw new IOException("Failed to detect file encoding: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get file statistics
     */
    public TextFileStats getFileStats(File textFile) throws IOException {
        if (textFile == null || !textFile.exists()) {
            throw new IllegalArgumentException("Text file does not exist");
        }
        
        try {
            String content = extractText(textFile);
            
            TextFileStats stats = new TextFileStats();
            stats.fileSize = Files.size(textFile.toPath());
            stats.characterCount = content.length();
            stats.lineCount = content.split("\n").length;
            stats.wordCount = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;
            stats.paragraphCount = content.split("\n\\s*\n").length;
            
            return stats;
            
        } catch (IOException e) {
            throw new IOException("Failed to get file statistics: " + e.getMessage(), e);
        }
    }
    
    /**
     * Clean up extracted text
     */
    private String cleanExtractedText(String text) {
        if (text == null) {
            return "";
        }
        
        // Remove excessive whitespace
        text = text.replaceAll("\\r\\n", "\n");  // Normalize line endings
        text = text.replaceAll("\\r", "\n");     // Handle old Mac line endings
        text = text.replaceAll("[ \\t]+", " ");  // Replace multiple spaces/tabs with single space
        text = text.replaceAll("\\n[ \\t]+", "\n"); // Remove leading whitespace on lines
        text = text.replaceAll("[ \\t]+\\n", "\n"); // Remove trailing whitespace on lines
        text = text.replaceAll("\\n{3,}", "\n\n"); // Replace multiple newlines with double newline
        
        // Remove common text artifacts
        text = text.replaceAll("\\u00A0", " "); // Replace non-breaking spaces
        text = text.replaceAll("\\u2022", "•"); // Normalize bullet points
        text = text.replaceAll("\\u2013", "-"); // Replace en-dash with hyphen
        text = text.replaceAll("\\u2014", "-"); // Replace em-dash with hyphen
        text = text.replaceAll("\\u201C|\\u201D", "\""); // Replace smart quotes
        text = text.replaceAll("\\u2018|\\u2019", "'"); // Replace smart apostrophes
        
        // Remove control characters except newlines and tabs
        text = text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        
        return text.trim();
    }
    
    /**
     * Inner class for file statistics
     */
    public static class TextFileStats {
        public long fileSize;
        public int characterCount;
        public int lineCount;
        public int wordCount;
        public int paragraphCount;
        
        @Override
        public String toString() {
            return String.format(
                "File Size: %d bytes\nCharacters: %d\nLines: %d\nWords: %d\nParagraphs: %d",
                fileSize, characterCount, lineCount, wordCount, paragraphCount
            );
        }
    }
}