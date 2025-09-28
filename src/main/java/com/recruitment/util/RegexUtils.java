package com.recruitment.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for regex-based text extraction
 */
public class RegexUtils {
    
    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    // Phone number regex patterns (various formats)
    private static final Pattern[] PHONE_PATTERNS = {
        // US formats: (123) 456-7890, 123-456-7890, 123.456.7890, 123 456 7890
        Pattern.compile("\\(?\\b\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4}\\b"),
        // International format: +1-123-456-7890, +1 123 456 7890
        Pattern.compile("\\+\\d{1,3}[-. ]?\\(?\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4}\\b"),
        // Simple format: 1234567890
        Pattern.compile("\\b\\d{10}\\b"),
        // Extended format with country code: +1 (123) 456-7890
        Pattern.compile("\\+\\d{1,3}\\s?\\(?\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4}\\b")
    };
    
    // URL regex pattern
    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://(?:[-\\w.])+(?:[:\\d]+)?(?:/(?:[\\w/_.])*(?:\\?(?:[\\w&=%.])*)?(?:#(?:[\\w.])*)?)?",
        Pattern.CASE_INSENSITIVE
    );
    
    // LinkedIn profile pattern
    private static final Pattern LINKEDIN_PATTERN = Pattern.compile(
        "(?:https?://)?(?:www\\.)?linkedin\\.com/in/[\\w-]+/?",
        Pattern.CASE_INSENSITIVE
    );
    
    // GitHub profile pattern
    private static final Pattern GITHUB_PATTERN = Pattern.compile(
        "(?:https?://)?(?:www\\.)?github\\.com/[\\w-]+/?",
        Pattern.CASE_INSENSITIVE
    );
    
    // Date patterns (various formats)
    private static final Pattern[] DATE_PATTERNS = {
        Pattern.compile("\\b\\d{1,2}/\\d{1,2}/\\d{4}\\b"), // MM/DD/YYYY
        Pattern.compile("\\b\\d{1,2}-\\d{1,2}-\\d{4}\\b"), // MM-DD-YYYY
        Pattern.compile("\\b\\d{4}-\\d{1,2}-\\d{1,2}\\b"), // YYYY-MM-DD
        Pattern.compile("\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{1,2},?\\s+\\d{4}\\b", Pattern.CASE_INSENSITIVE), // Month DD, YYYY
        Pattern.compile("\\b\\d{1,2}\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{4}\\b", Pattern.CASE_INSENSITIVE) // DD Month YYYY
    };
    
    // Years pattern (for experience, graduation, etc.)
    private static final Pattern YEARS_PATTERN = Pattern.compile(
        "\\b(19|20)\\d{2}\\b"
    );
    
    /**
     * Extract first email address from text
     */
    public static String extractEmail(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group().toLowerCase();
        }
        
        return null;
    }
    
    /**
     * Extract all email addresses from text
     */
    public static List<String> extractAllEmails(String text) {
        List<String> emails = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return emails;
        }
        
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        while (matcher.find()) {
            emails.add(matcher.group().toLowerCase());
        }
        
        return emails;
    }
    
    /**
     * Extract first phone number from text
     */
    public static String extractPhone(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        for (Pattern pattern : PHONE_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return cleanPhoneNumber(matcher.group());
            }
        }
        
        return null;
    }
    
    /**
     * Extract all phone numbers from text
     */
    public static List<String> extractAllPhones(String text) {
        List<String> phones = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return phones;
        }
        
        for (Pattern pattern : PHONE_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String phone = cleanPhoneNumber(matcher.group());
                if (!phones.contains(phone)) {
                    phones.add(phone);
                }
            }
        }
        
        return phones;
    }
    
    /**
     * Extract URLs from text
     */
    public static List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return urls;
        }
        
        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        
        return urls;
    }
    
    /**
     * Extract LinkedIn profile URL
     */
    public static String extractLinkedInProfile(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = LINKEDIN_PATTERN.matcher(text);
        if (matcher.find()) {
            String url = matcher.group();
            if (!url.startsWith("http")) {
                url = "https://" + url;
            }
            return url;
        }
        
        return null;
    }
    
    /**
     * Extract GitHub profile URL
     */
    public static String extractGitHubProfile(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = GITHUB_PATTERN.matcher(text);
        if (matcher.find()) {
            String url = matcher.group();
            if (!url.startsWith("http")) {
                url = "https://" + url;
            }
            return url;
        }
        
        return null;
    }
    
    /**
     * Extract dates from text
     */
    public static List<String> extractDates(String text) {
        List<String> dates = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return dates;
        }
        
        for (Pattern pattern : DATE_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String date = matcher.group();
                if (!dates.contains(date)) {
                    dates.add(date);
                }
            }
        }
        
        return dates;
    }
    
    /**
     * Extract years from text (useful for graduation years, work experience, etc.)
     */
    public static List<String> extractYears(String text) {
        List<String> years = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return years;
        }
        
        Matcher matcher = YEARS_PATTERN.matcher(text);
        while (matcher.find()) {
            String year = matcher.group();
            if (!years.contains(year)) {
                years.add(year);
            }
        }
        
        return years;
    }
    
    /**
     * Extract GPA from text
     */
    public static String extractGPA(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        // Pattern for GPA: 3.5, 3.50, GPA: 3.5, GPA 3.5/4.0, etc.
        Pattern gpaPattern = Pattern.compile(
            "(?i)(?:gpa|grade point average)\\s*:?\\s*(\\d\\.\\d{1,2})(?:/\\d\\.\\d{1,2})?",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = gpaPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Look for standalone GPA values (be more careful to avoid false positives)
        Pattern standaloneGpaPattern = Pattern.compile(
            "\\b([0-4]\\.[0-9]{1,2})\\s*/\\s*4\\.0\\b"
        );
        
        matcher = standaloneGpaPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Extract degree information from text
     */
    public static List<String> extractDegrees(String text) {
        List<String> degrees = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return degrees;
        }
        
        // Common degree patterns
        Pattern degreePattern = Pattern.compile(
            "(?i)\\b(?:bachelor|master|doctorate|phd|ph\\.d\\.|b\\.?[as]\\.|m\\.?[as]\\.|m\\.?b\\.?a\\.|b\\.?tech|m\\.?tech|b\\.?sc|m\\.?sc)\\b[^\\n]*",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = degreePattern.matcher(text);
        while (matcher.find()) {
            String degree = matcher.group().trim();
            if (degree.length() > 5 && degree.length() < 200) { // Reasonable length
                degrees.add(degree);
            }
        }
        
        return degrees;
    }
    
    /**
     * Extract certification information from text
     */
    public static List<String> extractCertifications(String text) {
        List<String> certifications = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return certifications;
        }
        
        // Look for certification keywords
        Pattern certPattern = Pattern.compile(
            "(?i)(?:certified?|certification|certificate)\\s+[^\\n]{10,100}",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = certPattern.matcher(text);
        while (matcher.find()) {
            String cert = matcher.group().trim();
            if (!certifications.contains(cert)) {
                certifications.add(cert);
            }
        }
        
        return certifications;
    }
    
    /**
     * Clean phone number by removing formatting
     */
    private static String cleanPhoneNumber(String phone) {
        if (phone == null) {
            return null;
        }
        
        // Remove all non-digit characters except + at the beginning
        String cleaned = phone.replaceAll("[^\\d+]", "");
        
        // If it starts with +, keep it, otherwise remove any + signs
        if (cleaned.startsWith("+")) {
            cleaned = "+" + cleaned.substring(1).replaceAll("\\+", "");
        } else {
            cleaned = cleaned.replaceAll("\\+", "");
        }
        
        return cleaned;
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        for (Pattern pattern : PHONE_PATTERNS) {
            if (pattern.matcher(phone.trim()).matches()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Extract text between two patterns
     */
    public static String extractBetween(String text, String startPattern, String endPattern) {
        if (text == null || startPattern == null || endPattern == null) {
            return null;
        }
        
        Pattern pattern = Pattern.compile(
            Pattern.quote(startPattern) + "(.*?)" + Pattern.quote(endPattern),
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return null;
    }
    
    /**
     * Remove HTML tags from text
     */
    public static String removeHtmlTags(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        return text.replaceAll("<[^>]+>", "").trim();
    }
    
    /**
     * Extract numbers from text
     */
    public static List<String> extractNumbers(String text) {
        List<String> numbers = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return numbers;
        }
        
        Pattern numberPattern = Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");
        Matcher matcher = numberPattern.matcher(text);
        
        while (matcher.find()) {
            numbers.add(matcher.group());
        }
        
        return numbers;
    }
}