package com.recruitment.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model class representing the result of matching a candidate to a job posting
 */
public class MatchResult implements Comparable<MatchResult> {
    private Long id;
    private Long candidateId;
    private Long jobId;
    private Candidate candidate;
    private JobPosting jobPosting;
    private double matchScore; // 0-100 percentage
    private int skillMatchCount;
    private int totalSkills;
    private boolean experienceMatch;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String matchSummary;
    private LocalDateTime calculatedAt;

    // Constructors
    public MatchResult() {
        this.matchedSkills = new ArrayList<>();
        this.missingSkills = new ArrayList<>();
        this.calculatedAt = LocalDateTime.now();
    }

    public MatchResult(Candidate candidate, JobPosting jobPosting) {
        this();
        this.candidate = candidate;
        this.jobPosting = jobPosting;
        this.candidateId = candidate.getId();
        this.jobId = jobPosting.getId();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
        if (candidate != null) {
            this.candidateId = candidate.getId();
        }
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
        if (jobPosting != null) {
            this.jobId = jobPosting.getId();
        }
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = Math.max(0, Math.min(100, matchScore)); // Ensure 0-100 range
    }

    public int getSkillMatchCount() {
        return skillMatchCount;
    }

    public void setSkillMatchCount(int skillMatchCount) {
        this.skillMatchCount = skillMatchCount;
    }

    public int getTotalSkills() {
        return totalSkills;
    }

    public void setTotalSkills(int totalSkills) {
        this.totalSkills = totalSkills;
    }

    public boolean isExperienceMatch() {
        return experienceMatch;
    }

    public void setExperienceMatch(boolean experienceMatch) {
        this.experienceMatch = experienceMatch;
    }

    public List<String> getMatchedSkills() {
        return new ArrayList<>(matchedSkills);
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = new ArrayList<>(matchedSkills);
    }

    public List<String> getMissingSkills() {
        return new ArrayList<>(missingSkills);
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = new ArrayList<>(missingSkills);
    }

    public String getMatchSummary() {
        return matchSummary;
    }

    public void setMatchSummary(String matchSummary) {
        this.matchSummary = matchSummary;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    // Utility methods
    public void addMatchedSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty() && !this.matchedSkills.contains(skill.trim())) {
            this.matchedSkills.add(skill.trim());
        }
    }

    public void addMissingSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty() && !this.missingSkills.contains(skill.trim())) {
            this.missingSkills.add(skill.trim());
        }
    }

    public double getSkillMatchPercentage() {
        if (totalSkills == 0) return 0;
        return (double) skillMatchCount / totalSkills * 100;
    }

    public String getMatchGrade() {
        if (matchScore >= 90) return "Excellent";
        if (matchScore >= 80) return "Very Good";
        if (matchScore >= 70) return "Good";
        if (matchScore >= 60) return "Fair";
        if (matchScore >= 50) return "Poor";
        return "Very Poor";
    }

    public boolean isStrongMatch() {
        return matchScore >= 80;
    }

    public boolean isGoodMatch() {
        return matchScore >= 70;
    }

    public boolean isFairMatch() {
        return matchScore >= 60;
    }

    /**
     * Generates a detailed match summary
     */
    public void generateSummary() {
        StringBuilder summary = new StringBuilder();
        
        summary.append(String.format("Match Score: %.1f%% (%s)\n", matchScore, getMatchGrade()));
        summary.append(String.format("Skills Match: %d/%d (%.1f%%)\n", 
                skillMatchCount, totalSkills, getSkillMatchPercentage()));
        summary.append(String.format("Experience Match: %s\n", 
                experienceMatch ? "✓ Meets requirement" : "✗ Below requirement"));
        
        if (!matchedSkills.isEmpty()) {
            summary.append(String.format("Matched Skills: %s\n", String.join(", ", matchedSkills)));
        }
        
        if (!missingSkills.isEmpty()) {
            summary.append(String.format("Missing Skills: %s\n", String.join(", ", missingSkills)));
        }
        
        this.matchSummary = summary.toString();
    }

    @Override
    public int compareTo(MatchResult other) {
        // Sort by match score in descending order (highest first)
        return Double.compare(other.matchScore, this.matchScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchResult that = (MatchResult) o;
        return Objects.equals(candidateId, that.candidateId) && 
               Objects.equals(jobId, that.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidateId, jobId);
    }

    @Override
    public String toString() {
        return String.format("MatchResult{candidate=%s, job=%s, score=%.1f%%, grade=%s}",
                candidate != null ? candidate.getName() : candidateId,
                jobPosting != null ? jobPosting.getTitle() : jobId,
                matchScore, getMatchGrade());
    }

    /**
     * Creates a detailed display string for UI purposes
     */
    public String getDetailedDisplay() {
        StringBuilder display = new StringBuilder();
        
        if (candidate != null) {
            display.append(String.format("Candidate: %s\n", candidate.getName()));
            display.append(String.format("Email: %s\n", candidate.getEmail()));
            display.append(String.format("Experience: %d years\n", candidate.getExperienceYears()));
        }
        
        display.append("\n");
        display.append(getMatchSummary() != null ? getMatchSummary() : "No summary available");
        
        return display.toString();
    }
}