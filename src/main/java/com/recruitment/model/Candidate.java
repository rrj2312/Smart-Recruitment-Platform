package com.recruitment.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model class representing a candidate in the recruitment system
 */
public class Candidate {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String education;
    private int experienceYears;
    private String resumeText;
    private List<String> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Candidate() {
        this.skills = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Candidate(String name, String email, String phone, String education, 
                    int experienceYears, String resumeText) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.education = education;
        this.experienceYears = experienceYears;
        this.resumeText = resumeText;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
        this.updatedAt = LocalDateTime.now();
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
        this.updatedAt = LocalDateTime.now();
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getSkills() {
        return new ArrayList<>(skills);
    }

    public void setSkills(List<String> skills) {
        this.skills = new ArrayList<>(skills);
        this.updatedAt = LocalDateTime.now();
    }

    public void addSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty() && !this.skills.contains(skill.trim())) {
            this.skills.add(skill.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeSkill(String skill) {
        this.skills.remove(skill);
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
    public boolean hasSkill(String skill) {
        return skills.stream()
                .anyMatch(s -> s.equalsIgnoreCase(skill.trim()));
    }

    public int getSkillCount() {
        return skills.size();
    }

    public boolean isExperienced(int requiredYears) {
        return this.experienceYears >= requiredYears;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return Objects.equals(email, candidate.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return String.format("Candidate{id=%d, name='%s', email='%s', experience=%d years, skills=%d}",
                id, name, email, experienceYears, skills.size());
    }

    /**
     * Creates a summary string for display purposes
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Name: %s\n", name));
        summary.append(String.format("Email: %s\n", email));
        if (phone != null && !phone.isEmpty()) {
            summary.append(String.format("Phone: %s\n", phone));
        }
        summary.append(String.format("Experience: %d years\n", experienceYears));
        if (education != null && !education.isEmpty()) {
            summary.append(String.format("Education: %s\n", education));
        }
        if (!skills.isEmpty()) {
            summary.append(String.format("Skills: %s\n", String.join(", ", skills)));
        }
        return summary.toString();
    }
}