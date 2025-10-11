package com.recruitment.ui;

import com.recruitment.database.CandidateDAO;
import com.recruitment.database.JobPostingDAO;
import com.recruitment.engine.MatchingEngine;
import com.recruitment.model.Candidate;
import com.recruitment.model.JobPosting;
import com.recruitment.model.MatchResult;
import com.recruitment.parser.ResumeParser;
import com.recruitment.util.ExcelExporter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Swing-based Recruiter Dashboard
 */
public class RecruiterDashboard extends JFrame {

    private CandidateDAO candidateDAO;
    private JobPostingDAO jobPostingDAO;
    private MatchingEngine matchingEngine;
    private ResumeParser resumeParser;

    // Tables and models
    private JTable candidatesTable;
    private DefaultTableModel candidatesModel;

    private JTable jobsTable;
    private DefaultTableModel jobsModel;

    private JTable matchResultsTable;
    private DefaultTableModel matchResultsModel;

    private JTextField candidateSearchField;
    private JLabel statusLabel;

    public RecruiterDashboard() {
        setTitle("Smart Recruitment Platform - Recruiter Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Initialize DAOs and engine
        candidateDAO = new CandidateDAO();
        jobPostingDAO = new JobPostingDAO();
        matchingEngine = new MatchingEngine();
        resumeParser = new ResumeParser();

        initUI();
        loadInitialData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));

        JLabel titleLabel = new JLabel("Smart Recruitment Platform");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Intelligent Resume Parsing & Candidate Matching");
        subtitleLabel.setForeground(new Color(189, 195, 199));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        headerPanel.add(titleLabel, gbc);
        gbc.gridy = 1;
        headerPanel.add(subtitleLabel, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Candidates", createCandidatesTab());
        tabbedPane.addTab("Jobs", createJobsTab());
        tabbedPane.addTab("Matching", createMatchingTab());
        tabbedPane.addTab("Reports", createReportsTab());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(236, 240, 241));
        statusLabel = new JLabel("Ready");
        statusBar.add(statusLabel);
        mainPanel.add(statusBar, BorderLayout.SOUTH);
    }

    /** -------------------- CANDIDATES TAB -------------------- **/
    private JPanel createCandidatesTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton uploadResumeBtn = new JButton("Upload Resume");
        uploadResumeBtn.addActionListener(e -> uploadResume());

        JButton addCandidateBtn = new JButton("Add Candidate");
        addCandidateBtn.addActionListener(e -> showAddCandidateDialog());

        JButton editCandidateBtn = new JButton("Edit Candidate");
        editCandidateBtn.addActionListener(e -> editSelectedCandidate());

        JButton deleteCandidateBtn = new JButton("Delete Candidate");
        deleteCandidateBtn.addActionListener(e -> deleteSelectedCandidate());

        JButton exportBtn = new JButton("Export to Excel");
        exportBtn.addActionListener(e -> exportCandidates());

        candidateSearchField = new JTextField(20);
        candidateSearchField.setToolTipText("Search candidates...");
        candidateSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { searchCandidates(); }
            public void removeUpdate(DocumentEvent e) { searchCandidates(); }
            public void insertUpdate(DocumentEvent e) { searchCandidates(); }
        });

        toolbar.add(uploadResumeBtn);
        toolbar.add(addCandidateBtn);
        toolbar.add(editCandidateBtn);
        toolbar.add(deleteCandidateBtn);
        toolbar.add(exportBtn);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(candidateSearchField);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Email", "Phone", "Experience", "Skills"};
        candidatesModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        candidatesTable = new JTable(candidatesModel);
        candidatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candidatesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showCandidateDetails(getSelectedCandidate());
                }
            }
        });
        panel.add(new JScrollPane(candidatesTable), BorderLayout.CENTER);

        return panel;
    }

    /** -------------------- JOBS TAB -------------------- **/
    private JPanel createJobsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addJobBtn = new JButton("Add Job");
        addJobBtn.addActionListener(e -> showAddJobDialog());

        JButton editJobBtn = new JButton("Edit Job");
        editJobBtn.addActionListener(e -> editSelectedJob());

        JButton deactivateJobBtn = new JButton("Activate/Deactivate");
        deactivateJobBtn.addActionListener(e -> toggleSelectedJob());

        JButton exportBtn = new JButton("Export Jobs");
        exportBtn.addActionListener(e -> exportJobs());

        toolbar.add(addJobBtn);
        toolbar.add(editJobBtn);
        toolbar.add(deactivateJobBtn);
        toolbar.add(exportBtn);

        panel.add(toolbar, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Location", "Experience", "Salary Min", "Salary Max", "Active"};
        jobsModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        jobsTable = new JTable(jobsModel);
        jobsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showJobDetails(getSelectedJob());
                }
            }
        });
        panel.add(new JScrollPane(jobsTable), BorderLayout.CENTER);

        return panel;
    }

    /** -------------------- MATCHING TAB -------------------- **/
    private JPanel createMatchingTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton findMatchesBtn = new JButton("Find Matches");
        findMatchesBtn.addActionListener(e -> findMatches(getSelectedJob()));

        JButton exportMatchesBtn = new JButton("Export Matches");
        exportMatchesBtn.addActionListener(e -> exportMatches(getSelectedJob()));

        toolbar.add(findMatchesBtn);
        toolbar.add(exportMatchesBtn);

        panel.add(toolbar, BorderLayout.NORTH);

        String[] columns = {"Candidate", "Email", "Score", "Grade", "Skills Match", "Experience"};
        matchResultsModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        matchResultsTable = new JTable(matchResultsModel);
        panel.add(new JScrollPane(matchResultsTable), BorderLayout.CENTER);

        return panel;
    }

    /** -------------------- REPORTS TAB -------------------- **/
    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Reports functionality - Coming Soon!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    /** -------------------- DATA LOADING -------------------- **/
    private void loadInitialData() {
        loadCandidates();
        loadJobs();
        statusLabel.setText("Data loaded successfully");
    }

    private void loadCandidates() {
        try {
            List<Candidate> candidates = candidateDAO.findAll();
            candidatesModel.setRowCount(0);
            for (Candidate c : candidates) {
                candidatesModel.addRow(new Object[]{
                        c.getId(),
                        c.getName(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getExperienceYears() + " years",
                        String.join(", ", c.getSkills())
                });
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load candidates: " + e.getMessage());
        }
    }

    private void loadJobs() {
        try {
            List<JobPosting> jobs = jobPostingDAO.findAll();
            jobsModel.setRowCount(0);
            for (JobPosting j : jobs) {
                jobsModel.addRow(new Object[]{
                        j.getId(),
                        j.getTitle(),
                        j.getLocation(),
                        j.getRequiredExperience() + " years",
                        j.getSalaryMin() != null ? "$" + j.getSalaryMin() : "N/A",
                        j.getSalaryMax() != null ? "$" + j.getSalaryMax() : "N/A",
                        j.isActive() ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load jobs: " + e.getMessage());
        }
    }

    /** -------------------- CANDIDATE METHODS -------------------- **/
    private void uploadResume() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || 
                       f.getName().toLowerCase().endsWith(".pdf") ||
                       f.getName().toLowerCase().endsWith(".docx") ||
                       f.getName().toLowerCase().endsWith(".txt");
            }
            public String getDescription() {
                return "Resume files (*.pdf, *.docx, *.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Candidate candidate = resumeParser.parseResume(file);
                if (candidate != null) {
                    candidateDAO.save(candidate);
                    loadCandidates();
                    statusLabel.setText("Resume uploaded and parsed: " + candidate.getName());
                }
            } catch (Exception e) {
                showError("Parse Error", "Failed to parse resume: " + e.getMessage());
            }
        }
    }

    private void showAddCandidateDialog() {
        JOptionPane.showMessageDialog(this, "Add Candidate dialog (to implement)");
    }

    private void editSelectedCandidate() {
        Candidate candidate = getSelectedCandidate();
        if (candidate != null) {
            JOptionPane.showMessageDialog(this, "Edit Candidate dialog for: " + candidate.getName());
        }
    }

    private void deleteSelectedCandidate() {
        Candidate candidate = getSelectedCandidate();
        if (candidate != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete " + candidate.getName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    candidateDAO.delete(candidate.getId());
                    loadCandidates();
                    statusLabel.setText("Candidate deleted: " + candidate.getName());
                } catch (SQLException e) {
                    showError("Database Error", "Failed to delete candidate: " + e.getMessage());
                }
            }
        }
    }

    private void searchCandidates() {
        String query = candidateSearchField.getText().trim().toLowerCase();
        for (int i = 0; i < candidatesModel.getRowCount(); i++) {
            boolean match = false;
            for (int j = 1; j < candidatesModel.getColumnCount(); j++) {
                String value = candidatesModel.getValueAt(i, j).toString().toLowerCase();
                if (value.contains(query)) {
                    match = true;
                    break;
                }
            }
            candidatesTable.setRowHeight(i, match ? 25 : 0);
        }
    }

    private void exportCandidates() {
        try {
            ExcelExporter.exportCandidates(candidateDAO.findAll(), new File("exports/candidates.xlsx"));
            statusLabel.setText("Candidates exported to Excel");
        } catch (Exception e) {
            showError("Export Error", "Failed to export candidates: " + e.getMessage());
        }
    }

    /** -------------------- JOB METHODS -------------------- **/
    private void showAddJobDialog() {
        JOptionPane.showMessageDialog(this, "Add Job dialog (to implement)");
    }

    private void editSelectedJob() {
        JobPosting job = getSelectedJob();
        if (job != null) {
            JOptionPane.showMessageDialog(this, "Edit Job dialog for: " + job.getTitle());
        }
    }

    private void toggleSelectedJob() {
        JobPosting job = getSelectedJob();
        if (job != null) {
            try {
                job.setActive(!job.isActive());
                jobPostingDAO.update(job);
                loadJobs();
                statusLabel.setText("Job status updated: " + job.getTitle());
            } catch (SQLException e) {
                showError("Database Error", "Failed to update job: " + e.getMessage());
            }
        }
    }

    private JobPosting getSelectedJob() {
        int row = jobsTable.getSelectedRow();
        if (row >= 0) {
            Long id = (Long) jobsModel.getValueAt(row, 0);
            try {
                return jobPostingDAO.findById(id);
            } catch (SQLException e) {
                showError("Database Error", "Failed to load job: " + e.getMessage());
            }
        }
        return null;
    }

    private void exportJobs() {
        try {
            ExcelExporter.exportJobPostings(jobPostingDAO.findAll(), new File("exports/jobs.xlsx"));
            statusLabel.setText("Jobs exported to Excel");
        } catch (Exception e) {
            showError("Export Error", "Failed to export jobs: " + e.getMessage());
        }
    }

    /** -------------------- MATCHING METHODS -------------------- **/
    private void findMatches(JobPosting job) {
        if (job == null) {
            JOptionPane.showMessageDialog(this, "Please select a job posting first.");
            return;
        }
        try {
            List<Candidate> allCandidates = candidateDAO.findAll();
            List<MatchResult> results = matchingEngine.findBestMatches(job, allCandidates, 10);
            matchResultsModel.setRowCount(0);
            for (MatchResult r : results) {
                matchResultsModel.addRow(new Object[]{
                        r.getCandidate().getName(),
                        r.getCandidate().getEmail(),
                        String.format("%.1f%%", r.getMatchScore()),
                        r.getMatchGrade(),
                        String.join(", ", r.getMatchedSkills()),
                        r.isExperienceMatch() ? "✓" : "✗"
                });
            }
            statusLabel.setText("Matches found for job: " + job.getTitle());
        } catch (Exception e) {
            showError("Matching Error", e.getMessage());
        }
    }

    private void exportMatches(JobPosting job) {
        if (job != null) {
            try {
                // Create a simple export for match results
                JOptionPane.showMessageDialog(this, "Match export functionality needs to be implemented");
                statusLabel.setText("Matches exported for job: " + job.getTitle());
            } catch (Exception e) {
                showError("Export Error", "Failed to export matches: " + e.getMessage());
            }
        }
    }

    /** -------------------- HELPER METHODS -------------------- **/
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    private void showCandidateDetails(Candidate candidate) {
        if (candidate != null) {
            JOptionPane.showMessageDialog(this, candidate.getSummary(), "Candidate Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showJobDetails(JobPosting job) {
        if (job != null) {
            JOptionPane.showMessageDialog(this, job.getSummary(), "Job Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private Candidate getSelectedCandidate() {
        int row = candidatesTable.getSelectedRow();
        if (row >= 0) {
            Long id = (Long) candidatesModel.getValueAt(row, 0);
            try {
                return candidateDAO.findById(id);
            } catch (SQLException e) {
                showError("Database Error", "Failed to load candidate: " + e.getMessage());
            }
        }
        return null;
    }
}