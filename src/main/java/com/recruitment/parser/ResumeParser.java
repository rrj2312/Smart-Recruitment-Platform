package com.recruitment.parser;

import com.recruitment.model.Candidate;
import com.recruitment.util.RegexUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main resume parser that delegates to specific format parsers
 */
public class ResumeParser {
    private final PDFParser pdfParser;
    private final DOCXParser docxParser;
    private final TextParser textParser;
    
    // Common skill keywords to look for in resumes
    private static final List<String> COMMON_SKILLS = Arrays.asList(
        "Java", "Python", "JavaScript", "C++", "C#", "PHP", "Ruby", "Go", "Kotlin", "Swift",
        "React", "Angular", "Vue", "Node.js", "Express", "Spring", "Django", "Flask", "Laravel",
        "HTML", "CSS", "Bootstrap", "Tailwind", "SASS", "LESS",
        "MySQL", "PostgreSQL", "MongoDB", "Redis", "SQLite", "Oracle", "SQL Server",
        "AWS", "Azure", "GCP", "Docker", "Kubernetes", "Jenkins", "Git", "Linux", "Windows",
        "Machine Learning", "AI", "Data Science", "TensorFlow", "PyTorch", "Pandas", "NumPy",
        "Agile", "Scrum", "DevOps", "CI/CD", "REST", "GraphQL", "Microservices", "API"
    );

    public ResumeParser() {
        this.pdfParser = new PDFParser();
        this.docxParser = new DOCXParser();
        this.textParser = new TextParser();
    }

    /**
     * Parse resume from file and extract candidate information
     */
    public Candidate parseResume(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }

        String fileName = file.getName().toLowerCase();
        String resumeText;

        // Determine file type and parse accordingly
        if (fileName.endsWith(".pdf")) {
            resumeText = pdfParser.extractText(file);
        } else if (fileName.endsWith(".docx")) {
            resumeText = docxParser.extractText(file);
        } else if (fileName.endsWith(".txt")) {
            resumeText = textParser.extractText(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Supported formats: PDF, DOCX, TXT");
        }

        return parseResumeText(resumeText);
    }

    /**
     * Parse resume from text content
     */
    public Candidate parseResumeText(String resumeText) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text cannot be empty");
        }

        Candidate candidate = new Candidate();
        candidate.setResumeText(resumeText);

        // Extract basic information
        extractName(candidate, resumeText);
        extractEmail(candidate, resumeText);
        extractPhone(candidate, resumeText);
        extractEducation(candidate, resumeText);
        extractExperience(candidate, resumeText);
        extractSkills(candidate, resumeText);

        return candidate;
    }

    /**
     * Extract candidate name from resume text
     */
    private void extractName(Candidate candidate, String text) {
        // Look for name patterns at the beginning of the resume
        String[] lines = text.split("\n");
        
        for (int i = 0; i < Math.min(5, lines.length); i++) {
            String line = lines[i].trim();
            
            // Skip empty lines and lines that look like headers
            if (line.isEmpty() || line.toLowerCase().contains("resume") || 
                line.toLowerCase().contains("curriculum vitae") || line.toLowerCase().contains("cv")) {
                continue;
            }
            
            // Look for name pattern (2-4 words, mostly alphabetic)
            if (isLikelyName(line)) {
                candidate.setName(line);
                break;
            }
        }
        
        // If no name found, try to extract from email
        if (candidate.getName() == null || candidate.getName().isEmpty()) {
            String email = RegexUtils.extractEmail(text);
            if (email != null) {
                String nameFromEmail = extractNameFromEmail(email);
                candidate.setName(nameFromEmail);
            }
        }
    }

    /**
     * Check if a line is likely to be a person's name
     */
    private boolean isLikelyName(String line) {
        // Remove common prefixes and suffixes
        line = line.replaceAll("(?i)^(mr\\.?|mrs\\.?|ms\\.?|dr\\.?|prof\\.?)\\s+", "");
        line = line.replaceAll("(?i)\\s+(jr\\.?|sr\\.?|ii|iii|iv)$", "");
        
        String[] words = line.split("\\s+");
        
        // Name should have 2-4 words
        if (words.length < 2 || words.length > 4) {
            return false;
        }
        
        // Each word should be mostly alphabetic and capitalized
        for (String word : words) {
            if (!word.matches("[A-Z][a-z]+") || word.length() < 2) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Extract name from email address
     */
    private String extractNameFromEmail(String email) {
        String localPart = email.split("@")[0];
        
        // Replace common separators with spaces
        localPart = localPart.replaceAll("[._-]", " ");
        
        // Capitalize each word
        String[] words = localPart.split("\\s+");
        StringBuilder name = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                name.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
            }
        }
        
        return name.toString().trim();
    }

    /**
     * Extract email address from resume text
     */
    private void extractEmail(Candidate candidate, String text) {
        String email = RegexUtils.extractEmail(text);
        candidate.setEmail(email);
    }

    /**
     * Extract phone number from resume text
     */
    private void extractPhone(Candidate candidate, String text) {
        String phone = RegexUtils.extractPhone(text);
        candidate.setPhone(phone);
    }

    /**
     * Extract education information from resume text
     */
    private void extractEducation(Candidate candidate, String text) {
        List<String> educationKeywords = Arrays.asList(
            "bachelor", "master", "phd", "doctorate", "degree", "university", "college",
            "b.s.", "b.a.", "m.s.", "m.a.", "m.b.a.", "ph.d.", "b.tech", "m.tech"
        );
        
        String[] lines = text.toLowerCase().split("\n");
        StringBuilder education = new StringBuilder();
        
        for (String line : lines) {
            for (String keyword : educationKeywords) {
                if (line.contains(keyword)) {
                    // Clean up the line and add to education
                    String cleanLine = line.trim().replaceAll("\\s+", " ");
                    if (cleanLine.length() > 10 && cleanLine.length() < 200) {
                        education.append(cleanLine).append("; ");
                        break;
                    }
                }
            }
        }
        
        if (education.length() > 0) {
            candidate.setEducation(education.toString().trim());
        }
    }

    /**
     * Extract years of experience from resume text
     */
    private void extractExperience(Candidate candidate, String text) {
        // Look for experience patterns
        Pattern experiencePattern = Pattern.compile(
            "(?i)(\\d+)\\s*(?:\\+)?\\s*years?\\s*(?:of\\s*)?(?:experience|exp)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = experiencePattern.matcher(text);
        int maxExperience = 0;
        
        while (matcher.find()) {
            try {
                int years = Integer.parseInt(matcher.group(1));
                maxExperience = Math.max(maxExperience, years);
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }
        
        // Also look for date ranges to estimate experience
        Pattern dateRangePattern = Pattern.compile(
            "(?i)(\\d{4})\\s*[-–—]\\s*(\\d{4}|present|current)",
            Pattern.CASE_INSENSITIVE
        );
        
        matcher = dateRangePattern.matcher(text);
        int estimatedExperience = 0;
        
        while (matcher.find()) {
            try {
                int startYear = Integer.parseInt(matcher.group(1));
                int endYear = matcher.group(2).toLowerCase().contains("present") || 
                             matcher.group(2).toLowerCase().contains("current") ? 
                             java.time.Year.now().getValue() : Integer.parseInt(matcher.group(2));
                
                estimatedExperience += Math.max(0, endYear - startYear);
            } catch (NumberFormatException e) {
                // Ignore invalid dates
            }
        }
        
        candidate.setExperienceYears(Math.max(maxExperience, estimatedExperience));
    }

    /**
     * Extract skills from resume text
     */
    private void extractSkills(Candidate candidate, String text) {
        List<String> foundSkills = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        // Look for common skills
        for (String skill : COMMON_SKILLS) {
            if (containsSkill(lowerText, skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }
        
        // Look for skills in dedicated sections
        extractSkillsFromSection(text, foundSkills);
        
        candidate.setSkills(foundSkills);
    }

    /**
     * Check if text contains a specific skill
     */
    private boolean containsSkill(String text, String skill) {
        // Create pattern that matches skill as whole word
        String pattern = "\\b" + Pattern.quote(skill) + "\\b";
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    /**
     * Extract skills from dedicated skills section
     */
    private void extractSkillsFromSection(String text, List<String> foundSkills) {
        // Look for skills section
        Pattern skillsSectionPattern = Pattern.compile(
            "(?i)(?:technical\\s+)?skills?\\s*:?\\s*([^\n]*(?:\n[^\n]*)*?)(?=\n\\s*[A-Z][^:\n]*:|$)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
        );
        
        Matcher matcher = skillsSectionPattern.matcher(text);
        
        while (matcher.find()) {
            String skillsSection = matcher.group(1);
            
            // Split by common delimiters
            String[] skills = skillsSection.split("[,;|•\\n]");
            
            for (String skill : skills) {
                skill = skill.trim().replaceAll("^[-•*]\\s*", "");
                
                if (skill.length() > 2 && skill.length() < 50 && 
                    !foundSkills.contains(skill) && isValidSkill(skill)) {
                    foundSkills.add(skill);
                }
            }
        }
    }

    /**
     * Check if a string is a valid skill
     */
    private boolean isValidSkill(String skill) {
        // Basic validation for skills
        return skill.matches("^[a-zA-Z0-9\\s\\.\\+\\#\\-]+$") && 
               !skill.toLowerCase().matches(".*\\b(and|or|the|with|in|of|for|to|at)\\b.*");
    }

    /**
     * Get supported file extensions
     */
    public static List<String> getSupportedExtensions() {
        return Arrays.asList("pdf", "docx", "txt");
    }

    /**
     * Check if file format is supported
     */
    public static boolean isSupported(String fileName) {
        if (fileName == null) return false;
        
        String extension = fileName.toLowerCase();
        int lastDot = extension.lastIndexOf('.');
        
        if (lastDot == -1) return false;
        
        extension = extension.substring(lastDot + 1);
        return getSupportedExtensions().contains(extension);
    }
}