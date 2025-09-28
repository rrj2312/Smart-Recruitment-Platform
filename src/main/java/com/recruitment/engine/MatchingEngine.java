package com.recruitment.engine;

import com.recruitment.model.Candidate;
import com.recruitment.model.JobPosting;
import com.recruitment.model.MatchResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Engine for matching candidates to job postings based on skills and experience
 */
public class MatchingEngine {
    
    // Weights for different matching criteria
    private static final double REQUIRED_SKILLS_WEIGHT = 0.6;  // 60%
    private static final double PREFERRED_SKILLS_WEIGHT = 0.2; // 20%
    private static final double EXPERIENCE_WEIGHT = 0.2;       // 20%
    
    // Bonus points
    private static final double EXPERIENCE_BONUS = 10.0; // Bonus for exceeding experience requirement
    private static final double PERFECT_SKILLS_BONUS = 5.0; // Bonus for having all required skills
    
    /**
     * Calculate match score between a candidate and job posting
     */
    public MatchResult calculateMatch(Candidate candidate, JobPosting jobPosting) {
        if (candidate == null || jobPosting == null) {
            throw new IllegalArgumentException("Candidate and job posting cannot be null");
        }
        
        MatchResult result = new MatchResult(candidate, jobPosting);
        
        // Calculate skill matches
        SkillMatchResult skillMatch = calculateSkillMatch(candidate, jobPosting);
        result.setSkillMatchCount(skillMatch.matchedCount);
        result.setTotalSkills(skillMatch.totalRequired);
        result.setMatchedSkills(skillMatch.matchedSkills);
        result.setMissingSkills(skillMatch.missingSkills);
        
        // Calculate experience match
        boolean experienceMatch = calculateExperienceMatch(candidate, jobPosting);
        result.setExperienceMatch(experienceMatch);
        
        // Calculate overall score
        double overallScore = calculateOverallScore(skillMatch, experienceMatch, candidate, jobPosting);
        result.setMatchScore(overallScore);
        
        // Generate summary
        result.generateSummary();
        
        return result;
    }
    
    /**
     * Find best matches for a job posting from a list of candidates
     */
    public List<MatchResult> findBestMatches(JobPosting jobPosting, List<Candidate> candidates, int maxResults) {
        if (jobPosting == null || candidates == null) {
            return new ArrayList<>();
        }
        
        List<MatchResult> matches = candidates.stream()
                .map(candidate -> calculateMatch(candidate, jobPosting))
                .sorted() // MatchResult implements Comparable (sorts by score descending)
                .limit(maxResults > 0 ? maxResults : candidates.size())
                .collect(Collectors.toList());
        
        return matches;
    }
    
    /**
     * Find all matches above a minimum score threshold
     */
    public List<MatchResult> findMatchesAboveThreshold(JobPosting jobPosting, List<Candidate> candidates, double minScore) {
        if (jobPosting == null || candidates == null) {
            return new ArrayList<>();
        }
        
        return candidates.stream()
                .map(candidate -> calculateMatch(candidate, jobPosting))
                .filter(match -> match.getMatchScore() >= minScore)
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Find suitable jobs for a candidate
     */
    public List<MatchResult> findSuitableJobs(Candidate candidate, List<JobPosting> jobPostings, int maxResults) {
        if (candidate == null || jobPostings == null) {
            return new ArrayList<>();
        }
        
        List<MatchResult> matches = jobPostings.stream()
                .filter(JobPosting::isActive) // Only consider active job postings
                .map(job -> calculateMatch(candidate, job))
                .sorted()
                .limit(maxResults > 0 ? maxResults : jobPostings.size())
                .collect(Collectors.toList());
        
        return matches;
    }
    
    /**
     * Calculate skill matching between candidate and job
     */
    private SkillMatchResult calculateSkillMatch(Candidate candidate, JobPosting jobPosting) {
        SkillMatchResult result = new SkillMatchResult();
        
        List<String> candidateSkills = candidate.getSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        
        List<String> requiredSkills = jobPosting.getRequiredSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        
        List<String> preferredSkills = jobPosting.getPreferredSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        
        // Check required skills
        for (String requiredSkill : requiredSkills) {
            if (candidateSkills.contains(requiredSkill)) {
                result.matchedSkills.add(requiredSkill);
                result.requiredMatched++;
            } else {
                result.missingSkills.add(requiredSkill);
            }
        }
        
        // Check preferred skills
        for (String preferredSkill : preferredSkills) {
            if (candidateSkills.contains(preferredSkill) && !result.matchedSkills.contains(preferredSkill)) {
                result.matchedSkills.add(preferredSkill);
                result.preferredMatched++;
            }
        }
        
        result.totalRequired = requiredSkills.size();
        result.totalPreferred = preferredSkills.size();
        result.matchedCount = result.requiredMatched + result.preferredMatched;
        
        return result;
    }
    
    /**
     * Calculate experience match
     */
    private boolean calculateExperienceMatch(Candidate candidate, JobPosting jobPosting) {
        return candidate.getExperienceYears() >= jobPosting.getRequiredExperience();
    }
    
    /**
     * Calculate overall match score
     */
    private double calculateOverallScore(SkillMatchResult skillMatch, boolean experienceMatch, 
                                       Candidate candidate, JobPosting jobPosting) {
        double score = 0.0;
        
        // Required skills score (0-60 points)
        if (skillMatch.totalRequired > 0) {
            double requiredSkillsScore = (double) skillMatch.requiredMatched / skillMatch.totalRequired * 100;
            score += requiredSkillsScore * REQUIRED_SKILLS_WEIGHT;
        } else {
            // If no required skills specified, give full points
            score += 100 * REQUIRED_SKILLS_WEIGHT;
        }
        
        // Preferred skills score (0-20 points)
        if (skillMatch.totalPreferred > 0) {
            double preferredSkillsScore = (double) skillMatch.preferredMatched / skillMatch.totalPreferred * 100;
            score += preferredSkillsScore * PREFERRED_SKILLS_WEIGHT;
        } else {
            // If no preferred skills specified, give full points
            score += 100 * PREFERRED_SKILLS_WEIGHT;
        }
        
        // Experience score (0-20 points)
        if (experienceMatch) {
            score += 100 * EXPERIENCE_WEIGHT;
            
            // Bonus for exceeding experience requirement
            int experienceExcess = candidate.getExperienceYears() - jobPosting.getRequiredExperience();
            if (experienceExcess > 0) {
                double bonus = Math.min(EXPERIENCE_BONUS, experienceExcess * 2.0);
                score += bonus;
            }
        } else {
            // Partial credit for some experience
            if (jobPosting.getRequiredExperience() > 0) {
                double experienceRatio = (double) candidate.getExperienceYears() / jobPosting.getRequiredExperience();
                score += Math.min(1.0, experienceRatio) * 100 * EXPERIENCE_WEIGHT;
            }
        }
        
        // Perfect skills bonus
        if (skillMatch.totalRequired > 0 && skillMatch.requiredMatched == skillMatch.totalRequired) {
            score += PERFECT_SKILLS_BONUS;
        }
        
        // Ensure score is within 0-100 range
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Get matching statistics for a job posting
     */
    public MatchingStats getMatchingStats(JobPosting jobPosting, List<Candidate> candidates) {
        if (jobPosting == null || candidates == null || candidates.isEmpty()) {
            return new MatchingStats();
        }
        
        List<MatchResult> matches = candidates.stream()
                .map(candidate -> calculateMatch(candidate, jobPosting))
                .collect(Collectors.toList());
        
        MatchingStats stats = new MatchingStats();
        stats.totalCandidates = candidates.size();
        stats.excellentMatches = (int) matches.stream().filter(m -> m.getMatchScore() >= 90).count();
        stats.goodMatches = (int) matches.stream().filter(m -> m.getMatchScore() >= 70 && m.getMatchScore() < 90).count();
        stats.fairMatches = (int) matches.stream().filter(m -> m.getMatchScore() >= 50 && m.getMatchScore() < 70).count();
        stats.poorMatches = stats.totalCandidates - stats.excellentMatches - stats.goodMatches - stats.fairMatches;
        
        if (!matches.isEmpty()) {
            stats.averageScore = matches.stream().mapToDouble(MatchResult::getMatchScore).average().orElse(0.0);
            stats.highestScore = matches.stream().mapToDouble(MatchResult::getMatchScore).max().orElse(0.0);
            stats.lowestScore = matches.stream().mapToDouble(MatchResult::getMatchScore).min().orElse(0.0);
        }
        
        return stats;
    }
    
    /**
     * Inner class for skill match results
     */
    private static class SkillMatchResult {
        int requiredMatched = 0;
        int preferredMatched = 0;
        int totalRequired = 0;
        int totalPreferred = 0;
        int matchedCount = 0;
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
    }
    
    /**
     * Statistics for matching results
     */
    public static class MatchingStats {
        public int totalCandidates = 0;
        public int excellentMatches = 0;  // 90-100%
        public int goodMatches = 0;       // 70-89%
        public int fairMatches = 0;       // 50-69%
        public int poorMatches = 0;       // 0-49%
        public double averageScore = 0.0;
        public double highestScore = 0.0;
        public double lowestScore = 0.0;
        
        @Override
        public String toString() {
            return String.format(
                "Total Candidates: %d\n" +
                "Excellent Matches (90-100%%): %d\n" +
                "Good Matches (70-89%%): %d\n" +
                "Fair Matches (50-69%%): %d\n" +
                "Poor Matches (0-49%%): %d\n" +
                "Average Score: %.1f%%\n" +
                "Highest Score: %.1f%%\n" +
                "Lowest Score: %.1f%%",
                totalCandidates, excellentMatches, goodMatches, fairMatches, poorMatches,
                averageScore, highestScore, lowestScore
            );
        }
    }
}