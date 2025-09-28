package com.recruitment.parser;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * DOCX parser using Apache POI
 */
public class DOCXParser {
    
    /**
     * Extract text content from DOCX file
     */
    public String extractText(File docxFile) throws IOException {
        if (docxFile == null || !docxFile.exists()) {
            throw new IllegalArgumentException("DOCX file does not exist");
        }
        
        if (!docxFile.getName().toLowerCase().endsWith(".docx")) {
            throw new IllegalArgumentException("File is not a DOCX document");
        }
        
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            StringBuilder extractedText = new StringBuilder();
            
            // Extract text from paragraphs
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                String paragraphText = paragraph.getText();
                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                    extractedText.append(paragraphText).append("\n");
                }
            }
            
            // Extract text from tables
            List<XWPFTable> tables = document.getTables();
            for (XWPFTable table : tables) {
                extractedText.append(extractTableText(table));
            }
            
            String result = extractedText.toString();
            
            if (result.trim().isEmpty()) {
                throw new IOException("No text content found in DOCX document");
            }
            
            return cleanExtractedText(result);
            
        } catch (IOException e) {
            throw new IOException("Failed to extract text from DOCX: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract text from a table
     */
    private String extractTableText(XWPFTable table) {
        StringBuilder tableText = new StringBuilder();
        
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (int i = 0; i < cells.size(); i++) {
                XWPFTableCell cell = cells.get(i);
                String cellText = cell.getText();
                
                if (cellText != null && !cellText.trim().isEmpty()) {
                    tableText.append(cellText);
                    
                    // Add separator between cells (except for last cell in row)
                    if (i < cells.size() - 1) {
                        tableText.append(" | ");
                    }
                }
            }
            tableText.append("\n");
        }
        
        return tableText.toString();
    }
    
    /**
     * Extract only paragraph text (excluding tables)
     */
    public String extractParagraphText(File docxFile) throws IOException {
        if (docxFile == null || !docxFile.exists()) {
            throw new IllegalArgumentException("DOCX file does not exist");
        }
        
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            StringBuilder extractedText = new StringBuilder();
            
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                String paragraphText = paragraph.getText();
                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                    extractedText.append(paragraphText).append("\n");
                }
            }
            
            return cleanExtractedText(extractedText.toString());
            
        } catch (IOException e) {
            throw new IOException("Failed to extract paragraph text from DOCX: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract only table text
     */
    public String extractTableText(File docxFile) throws IOException {
        if (docxFile == null || !docxFile.exists()) {
            throw new IllegalArgumentException("DOCX file does not exist");
        }
        
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            StringBuilder extractedText = new StringBuilder();
            
            List<XWPFTable> tables = document.getTables();
            for (XWPFTable table : tables) {
                extractedText.append(extractTableText(table));
                extractedText.append("\n");
            }
            
            return cleanExtractedText(extractedText.toString());
            
        } catch (IOException e) {
            throw new IOException("Failed to extract table text from DOCX: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get document properties/metadata
     */
    public String extractMetadata(File docxFile) throws IOException {
        if (docxFile == null || !docxFile.exists()) {
            throw new IllegalArgumentException("DOCX file does not exist");
        }
        
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            StringBuilder metadata = new StringBuilder();
            
            var properties = document.getProperties();
            if (properties != null) {
                var coreProps = properties.getCoreProperties();
                var extendedProps = properties.getExtendedProperties();
                
                if (coreProps != null) {
                    if (coreProps.getTitle() != null) {
                        metadata.append("Title: ").append(coreProps.getTitle()).append("\n");
                    }
                    if (coreProps.getCreator() != null) {
                        metadata.append("Author: ").append(coreProps.getCreator()).append("\n");
                    }
                    if (coreProps.getSubject() != null) {
                        metadata.append("Subject: ").append(coreProps.getSubject()).append("\n");
                    }
                    if (coreProps.getDescription() != null) {
                        metadata.append("Description: ").append(coreProps.getDescription()).append("\n");
                    }
                    if (coreProps.getCreated() != null) {
                        metadata.append("Created: ").append(coreProps.getCreated()).append("\n");
                    }
                    if (coreProps.getModified() != null) {
                        metadata.append("Modified: ").append(coreProps.getModified()).append("\n");
                    }
                }
                
                if (extendedProps != null && extendedProps.getUnderlyingProperties() != null) {
                    var underlying = extendedProps.getUnderlyingProperties();
                    if (underlying.getApplication() != null) {
                        metadata.append("Application: ").append(underlying.getApplication()).append("\n");
                    }
                    if (underlying.getPages() != null) {
                        metadata.append("Pages: ").append(underlying.getPages()).append("\n");
                    }
                    if (underlying.getWords() != null) {
                        metadata.append("Words: ").append(underlying.getWords()).append("\n");
                    }
                    if (underlying.getCharacters() != null) {
                        metadata.append("Characters: ").append(underlying.getCharacters()).append("\n");
                    }
                }
            }
            
            // Document statistics
            metadata.append("Paragraphs: ").append(document.getParagraphs().size()).append("\n");
            metadata.append("Tables: ").append(document.getTables().size()).append("\n");
            
            return metadata.toString();
            
        } catch (IOException e) {
            throw new IOException("Failed to extract DOCX metadata: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get paragraph count
     */
    public int getParagraphCount(File docxFile) throws IOException {
        if (docxFile == null || !docxFile.exists()) {
            throw new IllegalArgumentException("DOCX file does not exist");
        }
        
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            return document.getParagraphs().size();
            
        } catch (IOException e) {
            throw new IOException("Failed to count paragraphs in DOCX: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get table count
     */
    public int getTableCount(File docxFile) throws IOException {
        if (docxFile == null || !docxFile.exists()) {
            throw new IllegalArgumentException("DOCX file does not exist");
        }
        
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            return document.getTables().size();
            
        } catch (IOException e) {
            throw new IOException("Failed to count tables in DOCX: " + e.getMessage(), e);
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
        
        // Remove common document artifacts
        text = text.replaceAll("\\u00A0", " "); // Replace non-breaking spaces
        text = text.replaceAll("\\u2022", "•"); // Normalize bullet points
        text = text.replaceAll("\\u2013", "-"); // Replace en-dash with hyphen
        text = text.replaceAll("\\u2014", "-"); // Replace em-dash with hyphen
        text = text.replaceAll("\\u201C|\\u201D", "\""); // Replace smart quotes
        text = text.replaceAll("\\u2018|\\u2019", "'"); // Replace smart apostrophes
        
        return text.trim();
    }
}