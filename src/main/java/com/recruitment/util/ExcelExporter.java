package com.recruitment.util;

import com.recruitment.model.Candidate;
import com.recruitment.model.JobPosting;
import com.recruitment.model.MatchResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting data to Excel files using Apache POI
 */
public class ExcelExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Export candidates to Excel file
     */
    public static void exportCandidates(List<Candidate> candidates, File outputFile) throws IOException {
        if (candidates == null || outputFile == null) {
            throw new IllegalArgumentException("Candidates list and output file cannot be null");
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Candidates");
            
            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Name", "Email", "Phone", "Education", "Experience (Years)", 
                              "Skills", "Created At", "Updated At"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (Candidate candidate : candidates) {
                Row row = sheet.createRow(rowNum++);
                
                createCell(row, 0, candidate.getId() != null ? candidate.getId().toString() : "", dataStyle);
                createCell(row, 1, candidate.getName() != null ? candidate.getName() : "", dataStyle);
                createCell(row, 2, candidate.getEmail() != null ? candidate.getEmail() : "", dataStyle);
                createCell(row, 3, candidate.getPhone() != null ? candidate.getPhone() : "", dataStyle);
                createCell(row, 4, candidate.getEducation() != null ? candidate.getEducation() : "", dataStyle);
                createCell(row, 5, String.valueOf(candidate.getExperienceYears()), dataStyle);
                createCell(row, 6, String.join(", ", candidate.getSkills()), dataStyle);
                createCell(row, 7, candidate.getCreatedAt() != null ? 
                          candidate.getCreatedAt().format(DATE_FORMATTER) : "", dataStyle);
                createCell(row, 8, candidate.getUpdatedAt() != null ? 
                          candidate.getUpdatedAt().format(DATE_FORMATTER) : "", dataStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
        }
    }
    
    /**
     * Export job postings to Excel file
     */
    public static void exportJobPostings(List<JobPosting> jobPostings, File outputFile) throws IOException {
        if (jobPostings == null || outputFile == null) {
            throw new IllegalArgumentException("Job postings list and output file cannot be null");
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Job Postings");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Title", "Description", "Location", "Salary Min", "Salary Max", 
                              "Required Experience", "Required Skills", "Preferred Skills", "Active", 
                              "Created At", "Updated At"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (JobPosting job : jobPostings) {
                Row row = sheet.createRow(rowNum++);
                
                createCell(row, 0, job.getId() != null ? job.getId().toString() : "", dataStyle);
                createCell(row, 1, job.getTitle() != null ? job.getTitle() : "", dataStyle);
                createCell(row, 2, job.getDescription() != null ? job.getDescription() : "", dataStyle);
                createCell(row, 3, job.getLocation() != null ? job.getLocation() : "", dataStyle);
                createCell(row, 4, job.getSalaryMin() != null ? job.getSalaryMin().toString() : "", dataStyle);
                createCell(row, 5, job.getSalaryMax() != null ? job.getSalaryMax().toString() : "", dataStyle);
                createCell(row, 6, String.valueOf(job.getRequiredExperience()), dataStyle);
                createCell(row, 7, String.join(", ", job.getRequiredSkills()), dataStyle);
                createCell(row, 8, String.join(", ", job.getPreferredSkills()), dataStyle);
                createCell(row, 9, job.isActive() ? "Yes" : "No", dataStyle);
                createCell(row, 10, job.getCreatedAt() != null ? 
                          job.getCreatedAt().format(DATE_FORMATTER) : "", dataStyle);
                createCell(row, 11, job.getUpdatedAt() != null ? 
                          job.getUpdatedAt().format(DATE_FORMATTER) : "", dataStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
        }
    }
    
    /**
     * Export match results to Excel file
     */
    public static void exportMatchResults(List<MatchResult> matchResults, File outputFile) throws IOException {
        if (matchResults == null || outputFile == null) {
            throw new IllegalArgumentException("Match results list and output file cannot be null");
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Match Results");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle scoreStyle = createScoreStyle(workbook);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Rank", "Candidate Name", "Candidate Email", "Job Title", "Match Score (%)", 
                              "Match Grade", "Skills Matched", "Total Skills", "Experience Match", 
                              "Matched Skills", "Missing Skills", "Calculated At"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            int rank = 1;
            for (MatchResult match : matchResults) {
                Row row = sheet.createRow(rowNum++);
                
                createCell(row, 0, String.valueOf(rank++), dataStyle);
                createCell(row, 1, match.getCandidate() != null ? match.getCandidate().getName() : "", dataStyle);
                createCell(row, 2, match.getCandidate() != null ? match.getCandidate().getEmail() : "", dataStyle);
                createCell(row, 3, match.getJobPosting() != null ? match.getJobPosting().getTitle() : "", dataStyle);
                
                // Match score with special formatting
                Cell scoreCell = row.createCell(4);
                scoreCell.setCellValue(match.getMatchScore());
                scoreCell.setCellStyle(getScoreStyle(workbook, match.getMatchScore()));
                
                createCell(row, 5, match.getMatchGrade(), dataStyle);
                createCell(row, 6, String.valueOf(match.getSkillMatchCount()), dataStyle);
                createCell(row, 7, String.valueOf(match.getTotalSkills()), dataStyle);
                createCell(row, 8, match.isExperienceMatch() ? "Yes" : "No", dataStyle);
                createCell(row, 9, String.join(", ", match.getMatchedSkills()), dataStyle);
                createCell(row, 10, String.join(", ", match.getMissingSkills()), dataStyle);
                createCell(row, 11, match.getCalculatedAt() != null ? 
                          match.getCalculatedAt().format(DATE_FORMATTER) : "", dataStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Limit column width for better readability
                if (sheet.getColumnWidth(i) > 15000) {
                    sheet.setColumnWidth(i, 15000);
                }
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
        }
    }
    
    /**
     * Export shortlisted candidates for a specific job
     */
    public static void exportShortlistedCandidates(JobPosting job, List<MatchResult> shortlistedMatches, 
                                                 File outputFile) throws IOException {
        if (job == null || shortlistedMatches == null || outputFile == null) {
            throw new IllegalArgumentException("Job, shortlisted matches, and output file cannot be null");
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Job details sheet
            Sheet jobSheet = workbook.createSheet("Job Details");
            createJobDetailsSheet(jobSheet, job, workbook);
            
            // Shortlisted candidates sheet
            Sheet candidatesSheet = workbook.createSheet("Shortlisted Candidates");
            createShortlistedCandidatesSheet(candidatesSheet, shortlistedMatches, workbook);
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
        }
    }
    
    /**
     * Create job details sheet
     */
    private static void createJobDetailsSheet(Sheet sheet, JobPosting job, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // Job information
        createLabelValueRow(sheet, rowNum++, "Job Title", job.getTitle(), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Location", job.getLocation(), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Required Experience", job.getRequiredExperience() + " years", headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Salary Range", job.getSalaryRange(), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Required Skills", String.join(", ", job.getRequiredSkills()), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Preferred Skills", String.join(", ", job.getPreferredSkills()), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Description", job.getDescription(), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Created At", job.getCreatedAt().format(DATE_FORMATTER), headerStyle, dataStyle);
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    /**
     * Create shortlisted candidates sheet
     */
    private static void createShortlistedCandidatesSheet(Sheet sheet, List<MatchResult> matches, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Rank", "Name", "Email", "Phone", "Experience", "Match Score", "Grade", 
                          "Skills Match", "Experience Match", "Education", "Skills"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        int rank = 1;
        for (MatchResult match : matches) {
            Row row = sheet.createRow(rowNum++);
            Candidate candidate = match.getCandidate();
            
            createCell(row, 0, String.valueOf(rank++), dataStyle);
            createCell(row, 1, candidate.getName(), dataStyle);
            createCell(row, 2, candidate.getEmail(), dataStyle);
            createCell(row, 3, candidate.getPhone() != null ? candidate.getPhone() : "", dataStyle);
            createCell(row, 4, candidate.getExperienceYears() + " years", dataStyle);
            
            // Match score with color coding
            Cell scoreCell = row.createCell(5);
            scoreCell.setCellValue(String.format("%.1f%%", match.getMatchScore()));
            scoreCell.setCellStyle(getScoreStyle(workbook, match.getMatchScore()));
            
            createCell(row, 6, match.getMatchGrade(), dataStyle);
            createCell(row, 7, match.getSkillMatchCount() + "/" + match.getTotalSkills(), dataStyle);
            createCell(row, 8, match.isExperienceMatch() ? "✓" : "✗", dataStyle);
            createCell(row, 9, candidate.getEducation() != null ? candidate.getEducation() : "", dataStyle);
            createCell(row, 10, String.join(", ", candidate.getSkills()), dataStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) > 15000) {
                sheet.setColumnWidth(i, 15000);
            }
        }
    }
    
    /**
     * Create a label-value row
     */
    private static void createLabelValueRow(Sheet sheet, int rowNum, String label, String value, 
                                          CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowNum);
        
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value != null ? value : "");
        valueCell.setCellStyle(valueStyle);
    }
    
    /**
     * Create a cell with value and style
     */
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
    
    /**
     * Create header cell style
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Create data cell style
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        
        return style;
    }
    
    /**
     * Create score cell style
     */
    private static CellStyle createScoreStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    /**
     * Get score-based cell style with color coding
     */
    private static CellStyle getScoreStyle(Workbook workbook, double score) {
        CellStyle style = createScoreStyle(workbook);
        
        if (score >= 90) {
            style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        } else if (score >= 80) {
            style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        } else if (score >= 70) {
            style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        } else if (score < 50) {
            style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        }
        
        if (score >= 70 || score < 50) {
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        
        return style;
    }
    
    /**
     * Get default export filename for candidates
     */
    public static String getDefaultCandidatesFilename() {
        return "candidates_export_" + java.time.LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
    }
    
    /**
     * Get default export filename for job postings
     */
    public static String getDefaultJobPostingsFilename() {
        return "job_postings_export_" + java.time.LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
    }
    
    /**
     * Get default export filename for match results
     */
    public static String getDefaultMatchResultsFilename(String jobTitle) {
        String sanitizedTitle = jobTitle.replaceAll("[^a-zA-Z0-9]", "_");
        return "match_results_" + sanitizedTitle + "_" + java.time.LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
    }
}