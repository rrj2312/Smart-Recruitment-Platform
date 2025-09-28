package com.recruitment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model class representing a job posting in the recruitment system
 */
public class JobPosting {
    private Long id;
    private String title;
    private String description;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private int requiredExperience;
    private List<String> requiredSkills;
    private List<String> preferredSkills;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public JobPosting() {
        this.requiredSkills = new ArrayList<>();
        this.preferredSkills = new ArrayList<>();
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public JobPosting(String title, String description, String location, 
                     BigDecimal salaryMin, BigDecimal salaryMax, int requiredExperience) {
        this();
        this.title = title;
        this.description = description;
        this.location = location;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.requiredExperience = requiredExperience;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(BigDecimal salaryMin) {
        this.salaryMin = salaryMin;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(BigDecimal salaryMax) {
        this.salaryMax = salaryMax;
        this.updatedAt = LocalDateTime.now();
    }

    public int getRequiredExperience() {
        return requiredExperience;
    }

    public void setRequiredExperience(int requiredExperience) {
        this.requiredExperience = requiredExperience;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getRequiredSkills() {
        return new ArrayList<>(requiredSkills);
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = new ArrayList<>(requiredSkills);
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getPreferredSkills() {
        return new ArrayList<>(preferredSkills);
    }

    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = new ArrayList<>(preferredSkills);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public void addRequiredSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty() && !this.requiredSkills.contains(skill.trim())) {
            this.requiredSkills.add(skill.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addPreferredSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty() && !this.preferredSkills.contains(skill.trim())) {
            this.preferredSkills.add(skill.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeRequiredSkill(String skill) {
        this.requiredSkills.remove(skill);
        this.updatedAt = LocalDateTime.now();
    }

    public void removePreferredSkill(String skill) {
        this.preferredSkills.remove(skill);
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getAllSkills() {
        List<String> allSkills = new ArrayList<>(requiredSkills);
        allSkills.addAll(preferredSkills);
        return allSkills;
    }

    public boolean requiresSkill(String skill) {
        return requiredSkills.stream()
                .anyMatch(s -> s.equalsIgnoreCase(skill.trim()));
    }

    public boolean prefersSkill(String skill) {
        return preferredSkills.stream()
                .anyMatch(s -> s.equalsIgnoreCase(skill.trim()));
    }

    public String getSalaryRange() {
        if (salaryMin != null && salaryMax != null) {
            return String.format("$%,.0f - $%,.0f", salaryMin, salaryMax);
        } else if (salaryMin != null) {
            return String.format("$%,.0f+", salaryMin);
        } else if (salaryMax != null) {
            return String.format("Up to $%,.0f", salaryMax);
        }
        return "Salary not specified";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobPosting that = (JobPosting) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("JobPosting{id=%d, title='%s', location='%s', experience=%d years, skills=%d}",
                id, title, location, requiredExperience, getAllSkills().size());
    }

    /**
     * Creates a summary string for display purposes
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Title: %s\n", title));
        summary.append(String.format("Location: %s\n", location));
        summary.append(String.format("Experience Required: %d years\n", requiredExperience));
        summary.append(String.format("Salary: %s\n", getSalaryRange()));
        if (!requiredSkills.isEmpty()) {
            summary.append(String.format("Required Skills: %s\n", String.join(", ", requiredSkills)));
        }
        if (!preferredSkills.isEmpty()) {
            summary.append(String.format("Preferred Skills: %s\n", String.join(", ", preferredSkills)));
        }
        if (description != null && !description.isEmpty()) {
            summary.append(String.format("Description: %s\n", description));
        }
        return summary.toString();
    }
}