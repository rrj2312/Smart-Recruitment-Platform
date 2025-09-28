package com.recruitment.parser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

/**
 * PDF parser using Apache PDFBox
 */
public class PDFParser {
    
    /**
     * Extract text content from PDF file
     */
    public String extractText(File pdfFile) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException("PDF file does not exist");
        }
        
        if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("File is not a PDF");
        }
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            if (document.isEncrypted()) {
                throw new IOException("PDF is encrypted and cannot be processed");
            }
            
            PDFTextStripper textStripper = new PDFTextStripper();
            
            // Configure text stripper for better text extraction
            textStripper.setSortByPosition(true);
            textStripper.setLineSeparator("\n");
            textStripper.setWordSeparator(" ");
            textStripper.setArticleStart("");
            textStripper.setArticleEnd("");
            textStripper.setParagraphStart("");
            textStripper.setParagraphEnd("");
            textStripper.setPageStart("");
            textStripper.setPageEnd("");
            
            // Extract text from all pages
            String extractedText = textStripper.getText(document);
            
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new IOException("No text content found in PDF");
            }
            
            // Clean up the extracted text
            return cleanExtractedText(extractedText);
            
        } catch (IOException e) {
            throw new IOException("Failed to extract text from PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract text from specific page range
     */
    public String extractText(File pdfFile, int startPage, int endPage) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException("PDF file does not exist");
        }
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            if (document.isEncrypted()) {
                throw new IOException("PDF is encrypted and cannot be processed");
            }
            
            int totalPages = document.getNumberOfPages();
            
            // Validate page range
            if (startPage < 1 || endPage > totalPages || startPage > endPage) {
                throw new IllegalArgumentException(
                    String.format("Invalid page range: %d-%d (total pages: %d)", 
                                startPage, endPage, totalPages));
            }
            
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(startPage);
            textStripper.setEndPage(endPage);
            textStripper.setSortByPosition(true);
            
            String extractedText = textStripper.getText(document);
            
            return cleanExtractedText(extractedText);
            
        } catch (IOException e) {
            throw new IOException("Failed to extract text from PDF pages " + 
                                startPage + "-" + endPage + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Get number of pages in PDF
     */
    public int getPageCount(File pdfFile) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException("PDF file does not exist");
        }
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            throw new IOException("Failed to read PDF page count: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if PDF is encrypted
     */
    public boolean isEncrypted(File pdfFile) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException("PDF file does not exist");
        }
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.isEncrypted();
        } catch (IOException e) {
            throw new IOException("Failed to check PDF encryption status: " + e.getMessage(), e);
        }
    }
    
    /**
     * Clean up extracted text by removing extra whitespace and formatting issues
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
        
        // Remove common PDF artifacts
        text = text.replaceAll("\\f", ""); // Remove form feed characters
        text = text.replaceAll("\\u00A0", " "); // Replace non-breaking spaces
        text = text.replaceAll("\\u2022", "•"); // Normalize bullet points
        text = text.replaceAll("\\u2013", "-"); // Replace en-dash with hyphen
        text = text.replaceAll("\\u2014", "-"); // Replace em-dash with hyphen
        text = text.replaceAll("\\u201C|\\u201D", "\""); // Replace smart quotes
        text = text.replaceAll("\\u2018|\\u2019", "'"); // Replace smart apostrophes
        
        return text.trim();
    }
    
    /**
     * Extract metadata from PDF
     */
    public String extractMetadata(File pdfFile) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException("PDF file does not exist");
        }
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            StringBuilder metadata = new StringBuilder();
            
            if (document.getDocumentInformation() != null) {
                var info = document.getDocumentInformation();
                
                if (info.getTitle() != null) {
                    metadata.append("Title: ").append(info.getTitle()).append("\n");
                }
                if (info.getAuthor() != null) {
                    metadata.append("Author: ").append(info.getAuthor()).append("\n");
                }
                if (info.getSubject() != null) {
                    metadata.append("Subject: ").append(info.getSubject()).append("\n");
                }
                if (info.getCreator() != null) {
                    metadata.append("Creator: ").append(info.getCreator()).append("\n");
                }
                if (info.getCreationDate() != null) {
                    metadata.append("Created: ").append(info.getCreationDate()).append("\n");
                }
                if (info.getModificationDate() != null) {
                    metadata.append("Modified: ").append(info.getModificationDate()).append("\n");
                }
            }
            
            metadata.append("Pages: ").append(document.getNumberOfPages()).append("\n");
            metadata.append("Encrypted: ").append(document.isEncrypted()).append("\n");
            
            return metadata.toString();
            
        } catch (IOException e) {
            throw new IOException("Failed to extract PDF metadata: " + e.getMessage(), e);
        }
    }
}