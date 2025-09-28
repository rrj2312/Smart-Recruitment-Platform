package com.recruitment.ui;

import com.recruitment.database.CandidateDAO;
import com.recruitment.database.JobPostingDAO;
import com.recruitment.engine.MatchingEngine;
import com.recruitment.model.Candidate;
import com.recruitment.model.JobPosting;
import com.recruitment.model.MatchResult;
import com.recruitment.parser.ResumeParser;
import com.recruitment.util.ExcelExporter;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX-based Recruiter Dashboard
 */
public class RecruiterDashboard extends Application {
    
    private Stage primaryStage;
    private CandidateDAO candidateDAO;
    private JobPostingDAO jobPostingDAO;
    private MatchingEngine matchingEngine;
    private ResumeParser resumeParser;
    
    // UI Components
    private TabPane tabPane;
    private TableView<Candidate> candidatesTable;
    private TableView<JobPosting> jobsTable;
    private TableView<MatchResult> matchResultsTable;
    private ObservableList<Candidate> candidatesData;
    private ObservableList<JobPosting> jobsData;
    private ObservableList<MatchResult> matchResultsData;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialize components
        initializeComponents();
        
        // Create UI
        createUI();
        
        // Load initial data
        loadData();
        
        // Show the stage
        primaryStage.setTitle("Smart Recruitment Platform - Recruiter Dashboard");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    /**
     * Initialize components
     */
    private void initializeComponents() {
        candidateDAO = new CandidateDAO();
        jobPostingDAO = new JobPostingDAO();
        matchingEngine = new MatchingEngine();
        resumeParser = new ResumeParser();
        
        candidatesData = FXCollections.observableArrayList();
        jobsData = FXCollections.observableArrayList();
        matchResultsData = FXCollections.observableArrayList();
    }
    
    /**
     * Create the main UI
     */
    private void createUI() {
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Create header
        VBox header = createHeader();
        mainLayout.setTop(header);
        
        // Create tab pane
        tabPane = new TabPane();
        tabPane.getTabs().addAll(
            createCandidatesTab(),
            createJobsTab(),
            createMatchingTab(),
            createReportsTab()
        );
        
        mainLayout.setCenter(tabPane);
        
        // Create status bar
        HBox statusBar = createStatusBar();
        mainLayout.setBottom(statusBar);
        
        // Create scene
        Scene scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }
    
    /**
     * Create header section
     */
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        
        Label title = new Label("Smart Recruitment Platform");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitle = new Label("Intelligent Resume Parsing & Candidate Matching System");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #bdc3c7;");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    /**
     * Create candidates tab
     */
    private Tab createCandidatesTab() {
        Tab tab = new Tab("Candidates");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Button uploadResumeBtn = new Button("Upload Resume");
        uploadResumeBtn.setOnAction(e -> uploadResume());
        
        Button addCandidateBtn = new Button("Add Candidate");
        addCandidateBtn.setOnAction(e -> showAddCandidateDialog());
        
        Button editCandidateBtn = new Button("Edit Candidate");
        editCandidateBtn.setOnAction(e -> editSelectedCandidate());
        
        Button deleteCandidateBtn = new Button("Delete Candidate");
        deleteCandidateBtn.setOnAction(e -> deleteSelectedCandidate());
        
        Button exportBtn = new Button("Export to Excel");
        exportBtn.setOnAction(e -> exportCandidates());
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search candidates...");
        searchField.textProperty().addListener((obs, oldText, newText) -> searchCandidates(newText));
        
        toolbar.getChildren().addAll(uploadResumeBtn, addCandidateBtn, editCandidateBtn, 
                                   deleteCandidateBtn, exportBtn, new Separator(), searchField);
        
        // Candidates table
        candidatesTable = createCandidatesTable();
        
        content.getChildren().addAll(toolbar, candidatesTable);
        tab.setContent(content);
        
        return tab;
    }
    
    /**
     * Create jobs tab
     */
    private Tab createJobsTab() {
        Tab tab = new Tab("Job Postings");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Button addJobBtn = new Button("Add Job Posting");
        addJobBtn.setOnAction(e -> showAddJobDialog());
        
        Button editJobBtn = new Button("Edit Job");
        editJobBtn.setOnAction(e -> editSelectedJob());
        
        Button deactivateJobBtn = new Button("Deactivate Job");
        deactivateJobBtn.setOnAction(e -> deactivateSelectedJob());
        
        Button exportBtn = new Button("Export to Excel");
        exportBtn.setOnAction(e -> exportJobs());
        
        CheckBox showActiveOnly = new CheckBox("Show Active Only");
        showActiveOnly.setSelected(true);
        showActiveOnly.setOnAction(e -> filterJobs(showActiveOnly.isSelected()));
        
        toolbar.getChildren().addAll(addJobBtn, editJobBtn, deactivateJobBtn, exportBtn, 
                                   new Separator(), showActiveOnly);
        
        // Jobs table
        jobsTable = createJobsTable();
        
        content.getChildren().addAll(toolbar, jobsTable);
        tab.setContent(content);
        
        return tab;
    }
    
    /**
     * Create matching tab
     */
    private Tab createMatchingTab() {
        Tab tab = new Tab("Candidate Matching");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Job selection
        HBox jobSelection = new HBox(10);
        jobSelection.setAlignment(Pos.CENTER_LEFT);
        
        Label jobLabel = new Label("Select Job:");
        ComboBox<JobPosting> jobComboBox = new ComboBox<>();
        jobComboBox.setConverter(new StringConverter<JobPosting>() {
            @Override
            public String toString(JobPosting job) {
                return job != null ? job.getTitle() + " (" + job.getLocation() + ")" : "";
            }
            
            @Override
            public JobPosting fromString(String string) {
                return null;
            }
        });
        
        Button findMatchesBtn = new Button("Find Matches");
        findMatchesBtn.setOnAction(e -> findMatches(jobComboBox.getSelectionModel().getSelectedItem()));
        
        Button exportMatchesBtn = new Button("Export Matches");
        exportMatchesBtn.setOnAction(e -> exportMatches(jobComboBox.getSelectionModel().getSelectedItem()));
        
        jobSelection.getChildren().addAll(jobLabel, jobComboBox, findMatchesBtn, exportMatchesBtn);
        
        // Match results table
        matchResultsTable = createMatchResultsTable();
        
        content.getChildren().addAll(jobSelection, matchResultsTable);
        tab.setContent(content);
        
        // Update job combo box when jobs data changes
        jobsData.addListener((javafx.collections.ListChangeListener<JobPosting>) change -> {
            jobComboBox.setItems(FXCollections.observableArrayList(
                jobsData.filtered(JobPosting::isActive)
            ));
        });
        
        return tab;
    }
    
    /**
     * Create reports tab
     */
    private Tab createReportsTab() {
        Tab tab = new Tab("Reports & Analytics");
        tab.setClosable(false);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Statistics cards
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);
        
        VBox candidatesCard = createStatsCard("Total Candidates", "0", "#3498db");
        VBox jobsCard = createStatsCard("Active Jobs", "0", "#2ecc71");
        VBox matchesCard = createStatsCard("Total Matches", "0", "#e74c3c");
        
        statsCards.getChildren().addAll(candidatesCard, jobsCard, matchesCard);
        
        // Reports section
        VBox reportsSection = new VBox(10);
        Label reportsTitle = new Label("Export Reports");
        reportsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        HBox reportButtons = new HBox(10);
        Button exportAllCandidatesBtn = new Button("Export All Candidates");
        exportAllCandidatesBtn.setOnAction(e -> exportCandidates());
        
        Button exportAllJobsBtn = new Button("Export All Jobs");
        exportAllJobsBtn.setOnAction(e -> exportJobs());
        
        Button exportStatsBtn = new Button("Export Statistics");
        exportStatsBtn.setOnAction(e -> exportStatistics());
        
        reportButtons.getChildren().addAll(exportAllCandidatesBtn, exportAllJobsBtn, exportStatsBtn);
        
        reportsSection.getChildren().addAll(reportsTitle, reportButtons);
        
        content.getChildren().addAll(statsCards, new Separator(), reportsSection);
        tab.setContent(content);
        
        return tab;
    }
    
    /**
     * Create statistics card
     */
    private VBox createStatsCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");
        card.setPrefWidth(200);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    /**
     * Create status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #ecf0f1;");
        
        Label statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);
        
        return statusBar;
    }
    
    /**
     * Create candidates table
     */
    private TableView<Candidate> createCandidatesTable() {
        TableView<Candidate> table = new TableView<>();
        table.setItems(candidatesData);
        
        TableColumn<Candidate, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<Candidate, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<Candidate, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);
        
        TableColumn<Candidate, Integer> experienceCol = new TableColumn<>("Experience");
        experienceCol.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));
        experienceCol.setPrefWidth(100);
        
        TableColumn<Candidate, String> skillsCol = new TableColumn<>("Skills");
        skillsCol.setCellValueFactory(cellData -> {
            List<String> skills = cellData.getValue().getSkills();
            return new javafx.beans.property.SimpleStringProperty(
                skills.size() > 3 ? String.join(", ", skills.subList(0, 3)) + "..." : String.join(", ", skills)
            );
        });
        skillsCol.setPrefWidth(200);
        
        table.getColumns().addAll(nameCol, emailCol, phoneCol, experienceCol, skillsCol);
        
        // Double-click to view details
        table.setRowFactory(tv -> {
            TableRow<Candidate> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showCandidateDetails(row.getItem());
                }
            });
            return row;
        });
        
        return table;
    }
    
    /**
     * Create jobs table
     */
    private TableView<JobPosting> createJobsTable() {
        TableView<JobPosting> table = new TableView<>();
        table.setItems(jobsData);
        
        TableColumn<JobPosting, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);
        
        TableColumn<JobPosting, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(150);
        
        TableColumn<JobPosting, Integer> experienceCol = new TableColumn<>("Experience Required");
        experienceCol.setCellValueFactory(new PropertyValueFactory<>("requiredExperience"));
        experienceCol.setPrefWidth(130);
        
        TableColumn<JobPosting, String> salaryCol = new TableColumn<>("Salary Range");
        salaryCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSalaryRange())
        );
        salaryCol.setPrefWidth(150);
        
        TableColumn<JobPosting, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(80);
        
        table.getColumns().addAll(titleCol, locationCol, experienceCol, salaryCol, activeCol);
        
        // Double-click to view details
        table.setRowFactory(tv -> {
            TableRow<JobPosting> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showJobDetails(row.getItem());
                }
            });
            return row;
        });
        
        return table;
    }
    
    /**
     * Create match results table
     */
    private TableView<MatchResult> createMatchResultsTable() {
        TableView<MatchResult> table = new TableView<>();
        table.setItems(matchResultsData);
        
        TableColumn<MatchResult, String> candidateCol = new TableColumn<>("Candidate");
        candidateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCandidate() != null ? 
                cellData.getValue().getCandidate().getName() : ""
            )
        );
        candidateCol.setPrefWidth(150);
        
        TableColumn<MatchResult, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCandidate() != null ? 
                cellData.getValue().getCandidate().getEmail() : ""
            )
        );
        emailCol.setPrefWidth(200);
        
        TableColumn<MatchResult, Double> scoreCol = new TableColumn<>("Match Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("matchScore"));
        scoreCol.setCellFactory(column -> new TableCell<MatchResult, Double>() {
            @Override
            protected void updateItem(Double score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1f%%", score));
                    if (score >= 90) {
                        setStyle("-fx-background-color: #d5f4e6;");
                    } else if (score >= 80) {
                        setStyle("-fx-background-color: #fff3cd;");
                    } else if (score >= 70) {
                        setStyle("-fx-background-color: #f8d7da;");
                    }
                }
            }
        });
        scoreCol.setPrefWidth(100);
        
        TableColumn<MatchResult, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMatchGrade())
        );
        gradeCol.setPrefWidth(100);
        
        TableColumn<MatchResult, String> skillsMatchCol = new TableColumn<>("Skills Match");
        skillsMatchCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getSkillMatchCount() + "/" + cellData.getValue().getTotalSkills()
            )
        );
        skillsMatchCol.setPrefWidth(100);
        
        TableColumn<MatchResult, Boolean> experienceCol = new TableColumn<>("Experience Match");
        experienceCol.setCellValueFactory(new PropertyValueFactory<>("experienceMatch"));
        experienceCol.setPrefWidth(120);
        
        table.getColumns().addAll(candidateCol, emailCol, scoreCol, gradeCol, skillsMatchCol, experienceCol);
        
        // Double-click to view details
        table.setRowFactory(tv -> {
            TableRow<MatchResult> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showMatchDetails(row.getItem());
                }
            });
            return row;
        });
        
        return table;
    }
    
    /**
     * Load initial data
     */
    private void loadData() {
        try {
            // Load candidates
            List<Candidate> candidates = candidateDAO.findAll();
            candidatesData.setAll(candidates);
            
            // Load jobs
            List<JobPosting> jobs = jobPostingDAO.findAll();
            jobsData.setAll(jobs);
            
            // Update statistics
            updateStatistics();
            
        } catch (SQLException e) {
            showError("Database Error", "Failed to load data: " + e.getMessage());
        }
    }
    
    /**
     * Upload resume file
     */
    private void uploadResume() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Resume File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Supported", "*.pdf", "*.docx", "*.txt"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Documents", "*.docx"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            try {
                // Parse resume
                Candidate candidate = resumeParser.parseResume(selectedFile);
                
                // Show candidate details for review/editing
                if (showCandidateEditDialog(candidate, "Review Parsed Resume")) {
                    // Save candidate
                    candidateDAO.save(candidate);
                    candidatesData.add(candidate);
                    updateStatistics();
                    showInfo("Success", "Resume parsed and candidate added successfully!");
                }
                
            } catch (Exception e) {
                showError("Parse Error", "Failed to parse resume: " + e.getMessage());
            }
        }
    }
    
    /**
     * Show add candidate dialog
     */
    private void showAddCandidateDialog() {
        Candidate candidate = new Candidate();
        if (showCandidateEditDialog(candidate, "Add New Candidate")) {
            try {
                candidateDAO.save(candidate);
                candidatesData.add(candidate);
                updateStatistics();
                showInfo("Success", "Candidate added successfully!");
            } catch (SQLException e) {
                showError("Database Error", "Failed to save candidate: " + e.getMessage());
            }
        }
    }
    
    /**
     * Show candidate edit dialog
     */
    private boolean showCandidateEditDialog(Candidate candidate, String title) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Enter candidate information");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(candidate.getName());
        TextField emailField = new TextField(candidate.getEmail());
        TextField phoneField = new TextField(candidate.getPhone());
        TextField educationField = new TextField(candidate.getEducation());
        Spinner<Integer> experienceSpinner = new Spinner<>(0, 50, candidate.getExperienceYears());
        TextArea skillsArea = new TextArea(String.join(", ", candidate.getSkills()));
        skillsArea.setPrefRowCount(3);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Education:"), 0, 3);
        grid.add(educationField, 1, 3);
        grid.add(new Label("Experience (years):"), 0, 4);
        grid.add(experienceSpinner, 1, 4);
        grid.add(new Label("Skills (comma-separated):"), 0, 5);
        grid.add(skillsArea, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            candidate.setName(nameField.getText());
            candidate.setEmail(emailField.getText());
            candidate.setPhone(phoneField.getText());
            candidate.setEducation(educationField.getText());
            candidate.setExperienceYears(experienceSpinner.getValue());
            
            // Parse skills
            String skillsText = skillsArea.getText();
            if (skillsText != null && !skillsText.trim().isEmpty()) {
                List<String> skills = Arrays.asList(skillsText.split(","));
                candidate.setSkills(skills.stream().map(String::trim).toList());
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Edit selected candidate
     */
    private void editSelectedCandidate() {
        Candidate selected = candidatesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a candidate to edit.");
            return;
        }
        
        if (showCandidateEditDialog(selected, "Edit Candidate")) {
            try {
                candidateDAO.update(selected);
                candidatesTable.refresh();
                showInfo("Success", "Candidate updated successfully!");
            } catch (SQLException e) {
                showError("Database Error", "Failed to update candidate: " + e.getMessage());
            }
        }
    }
    
    /**
     * Delete selected candidate
     */
    private void deleteSelectedCandidate() {
        Candidate selected = candidatesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a candidate to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Candidate");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                candidateDAO.delete(selected.getId());
                candidatesData.remove(selected);
                updateStatistics();
                showInfo("Success", "Candidate deleted successfully!");
            } catch (SQLException e) {
                showError("Database Error", "Failed to delete candidate: " + e.getMessage());
            }
        }
    }
    
    /**
     * Show add job dialog
     */
    private void showAddJobDialog() {
        JobPosting job = new JobPosting();
        if (showJobEditDialog(job, "Add New Job Posting")) {
            try {
                jobPostingDAO.save(job);
                jobsData.add(job);
                updateStatistics();
                showInfo("Success", "Job posting added successfully!");
            } catch (SQLException e) {
                showError("Database Error", "Failed to save job posting: " + e.getMessage());
            }
        }
    }
    
    /**
     * Show job edit dialog
     */
    private boolean showJobEditDialog(JobPosting job, String title) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Enter job posting information");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField(job.getTitle());
        TextField locationField = new TextField(job.getLocation());
        TextArea descriptionArea = new TextArea(job.getDescription());
        descriptionArea.setPrefRowCount(3);
        TextField salaryMinField = new TextField(job.getSalaryMin() != null ? job.getSalaryMin().toString() : "");
        TextField salaryMaxField = new TextField(job.getSalaryMax() != null ? job.getSalaryMax().toString() : "");
        Spinner<Integer> experienceSpinner = new Spinner<>(0, 20, job.getRequiredExperience());
        TextArea requiredSkillsArea = new TextArea(String.join(", ", job.getRequiredSkills()));
        requiredSkillsArea.setPrefRowCount(2);
        TextArea preferredSkillsArea = new TextArea(String.join(", ", job.getPreferredSkills()));
        preferredSkillsArea.setPrefRowCount(2);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Location:"), 0, 1);
        grid.add(locationField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionArea, 1, 2);
        grid.add(new Label("Salary Min:"), 0, 3);
        grid.add(salaryMinField, 1, 3);
        grid.add(new Label("Salary Max:"), 0, 4);
        grid.add(salaryMaxField, 1, 4);
        grid.add(new Label("Required Experience:"), 0, 5);
        grid.add(experienceSpinner, 1, 5);
        grid.add(new Label("Required Skills:"), 0, 6);
        grid.add(requiredSkillsArea, 1, 6);
        grid.add(new Label("Preferred Skills:"), 0, 7);
        grid.add(preferredSkillsArea, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            job.setTitle(titleField.getText());
            job.setLocation(locationField.getText());
            job.setDescription(descriptionArea.getText());
            
            try {
                if (!salaryMinField.getText().isEmpty()) {
                    job.setSalaryMin(new BigDecimal(salaryMinField.getText()));
                }
                if (!salaryMaxField.getText().isEmpty()) {
                    job.setSalaryMax(new BigDecimal(salaryMaxField.getText()));
                }
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter valid salary amounts.");
                return false;
            }
            
            job.setRequiredExperience(experienceSpinner.getValue());
            
            // Parse skills
            String requiredSkillsText = requiredSkillsArea.getText();
            if (requiredSkillsText != null && !requiredSkillsText.trim().isEmpty()) {
                List<String> skills = Arrays.asList(requiredSkillsText.split(","));
                job.setRequiredSkills(skills.stream().map(String::trim).toList());
            }
            
            String preferredSkillsText = preferredSkillsArea.getText();
            if (preferredSkillsText != null && !preferredSkillsText.trim().isEmpty()) {
                List<String> skills = Arrays.asList(preferredSkillsText.split(","));
                job.setPreferredSkills(skills.stream().map(String::trim).toList());
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Edit selected job
     */
    private void editSelectedJob() {
        JobPosting selected = jobsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a job posting to edit.");
            return;
        }
        
        if (showJobEditDialog(selected, "Edit Job Posting")) {
            try {
                jobPostingDAO.update(selected);
                jobsTable.refresh();
                showInfo("Success", "Job posting updated successfully!");
            } catch (SQLException e) {
                showError("Database Error", "Failed to update job posting: " + e.getMessage());
            }
        }
    }
    
    /**
     * Deactivate selected job
     */
    private void deactivateSelectedJob() {
        JobPosting selected = jobsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a job posting to deactivate.");
            return;
        }
        
        try {
            if (selected.isActive()) {
                jobPostingDAO.deactivate(selected.getId());
                selected.setActive(false);
                showInfo("Success", "Job posting deactivated successfully!");
            } else {
                jobPostingDAO.activate(selected.getId());
                selected.setActive(true);
                showInfo("Success", "Job posting activated successfully!");
            }
            jobsTable.refresh();
            updateStatistics();
        } catch (SQLException e) {
            showError("Database Error", "Failed to update job posting: " + e.getMessage());
        }
    }
    
    /**
     * Find matches for selected job
     */
    private void findMatches(JobPosting selectedJob) {
        if (selectedJob == null) {
            showWarning("No Selection", "Please select a job posting to find matches.");
            return;
        }
        
        try {
            List<Candidate> allCandidates = candidateDAO.findAll();
            List<MatchResult> matches = matchingEngine.findBestMatches(selectedJob, allCandidates, 50);
            matchResultsData.setAll(matches);
            
            showInfo("Matches Found", String.format("Found %d potential matches for %s", 
                    matches.size(), selectedJob.getTitle()));
            
        } catch (SQLException e) {
            showError("Database Error", "Failed to find matches: " + e.getMessage());
        }
    }
    
    /**
     * Search candidates
     */
    private void searchCandidates(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<Candidate> results = candidateDAO.search(searchTerm);
            candidatesData.setAll(results);
        } catch (SQLException e) {
            showError("Search Error", "Failed to search candidates: " + e.getMessage());
        }
    }
    
    /**
     * Filter jobs by active status
     */
    private void filterJobs(boolean activeOnly) {
        try {
            List<JobPosting> jobs = activeOnly ? jobPostingDAO.findActive() : jobPostingDAO.findAll();
            jobsData.setAll(jobs);
        } catch (SQLException e) {
            showError("Filter Error", "Failed to filter jobs: " + e.getMessage());
        }
    }
    
    /**
     * Export candidates to Excel
     */
    private void exportCandidates() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Candidates");
        fileChooser.setInitialFileName(ExcelExporter.getDefaultCandidatesFilename());
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                ExcelExporter.exportCandidates(candidatesData, file);
                showInfo("Export Successful", "Candidates exported to " + file.getName());
            } catch (Exception e) {
                showError("Export Error", "Failed to export candidates: " + e.getMessage());
            }
        }
    }
    
    /**
     * Export jobs to Excel
     */
    private void exportJobs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Job Postings");
        fileChooser.setInitialFileName(ExcelExporter.getDefaultJobPostingsFilename());
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                ExcelExporter.exportJobPostings(jobsData, file);
                showInfo("Export Successful", "Job postings exported to " + file.getName());
            } catch (Exception e) {
                showError("Export Error", "Failed to export job postings: " + e.getMessage());
            }
        }
    }
    
    /**
     * Export match results
     */
    private void exportMatches(JobPosting selectedJob) {
        if (selectedJob == null || matchResultsData.isEmpty()) {
            showWarning("No Data", "Please select a job and find matches first.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Match Results");
        fileChooser.setInitialFileName(ExcelExporter.getDefaultMatchResultsFilename(selectedJob.getTitle()));
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                ExcelExporter.exportShortlistedCandidates(selectedJob, matchResultsData, file);
                showInfo("Export Successful", "Match results exported to " + file.getName());
            } catch (Exception e) {
                showError("Export Error", "Failed to export match results: " + e.getMessage());
            }
        }
    }
    
    /**
     * Export statistics
     */
    private void exportStatistics() {
        // Implementation for exporting statistics
        showInfo("Feature Coming Soon", "Statistics export feature will be available in the next version.");
    }
    
    /**
     * Show candidate details
     */
    private void showCandidateDetails(Candidate candidate) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Candidate Details");
        alert.setHeaderText(candidate.getName());
        alert.setContentText(candidate.getSummary());
        alert.showAndWait();
    }
    
    /**
     * Show job details
     */
    private void showJobDetails(JobPosting job) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Job Posting Details");
        alert.setHeaderText(job.getTitle());
        alert.setContentText(job.getSummary());
        alert.showAndWait();
    }
    
    /**
     * Show match details
     */
    private void showMatchDetails(MatchResult match) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Match Details");
        alert.setHeaderText(String.format("Match Score: %.1f%% (%s)", 
                match.getMatchScore(), match.getMatchGrade()));
        alert.setContentText(match.getDetailedDisplay());
        alert.showAndWait();
    }
    
    /**
     * Update statistics
     */
    private void updateStatistics() {
        // This would update the statistics cards in the reports tab
        // Implementation depends on how the cards are structured
    }
    
    /**
     * Show information dialog
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show warning dialog
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}