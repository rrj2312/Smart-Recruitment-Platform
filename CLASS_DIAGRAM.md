# Smart Recruitment Platform - Class Diagram

## Package Structure

```
com.recruitment/
├── model/
│   ├── Candidate.java
│   ├── JobPosting.java
│   └── MatchResult.java
├── parser/
│   ├── ResumeParser.java
│   ├── PDFParser.java
│   ├── DOCXParser.java
│   └── TextParser.java
├── engine/
│   └── MatchingEngine.java
├── database/
│   ├── DatabaseManager.java
│   ├── CandidateDAO.java
│   └── JobPostingDAO.java
├── ui/
│   ├── MainApplication.java
│   └── RecruiterDashboard.java
├── util/
│   ├── ExcelExporter.java
│   └── RegexUtils.java
└── Main.java
```

## Class Relationships

### Model Layer
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Candidate    │    │   JobPosting    │    │   MatchResult   │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ - id: Long      │    │ - id: Long      │    │ - id: Long      │
│ - name: String  │    │ - title: String │    │ - candidateId   │
│ - email: String │    │ - location      │    │ - jobId: Long   │
│ - phone: String │    │ - salaryMin     │    │ - matchScore    │
│ - education     │    │ - salaryMax     │    │ - skillMatch    │
│ - experience    │    │ - experience    │    │ - expMatch      │
│ - skills: List  │    │ - reqSkills     │    │ - candidate     │
│ - resumeText    │    │ - prefSkills    │    │ - jobPosting    │
│ - createdAt     │    │ - isActive      │    │ - matchedSkills │
│ - updatedAt     │    │ - createdAt     │    │ - missingSkills │
└─────────────────┘    │ - updatedAt     │    │ - calculatedAt  │
                       └─────────────────┘    └─────────────────┘
```

### Parser Layer
```
┌─────────────────┐
│  ResumeParser   │ ◄─────┐
├─────────────────┤       │
│ + parseResume() │       │
│ + parseText()   │       │
│ - extractName() │       │
│ - extractEmail()│       │
│ - extractPhone()│       │
│ - extractSkills│       │
└─────────────────┘       │
                          │
    ┌─────────────────────┼─────────────────────┐
    │                     │                     │
┌───▼──────┐    ┌────▼─────┐    ┌────▼─────┐
│PDFParser │    │DOCXParser│    │TextParser│
├──────────┤    ├──────────┤    ├──────────┤
│+extract  │    │+extract  │    │+extract  │
│Text()    │    │Text()    │    │Text()    │
└──────────┘    └──────────┘    └──────────┘
```

### Engine Layer
```
┌─────────────────┐
│ MatchingEngine  │
├─────────────────┤
│ + calculateMatch()      │
│ + findBestMatches()     │
│ + findMatchesAbove()    │
│ + findSuitableJobs()    │
│ + getMatchingStats()    │
│ - calculateSkillMatch() │
│ - calculateExpMatch()   │
│ - calculateOverall()    │
└─────────────────┘
```

### Database Layer
```
┌─────────────────┐
│ DatabaseManager │ ◄─────┐
├─────────────────┤       │
│ + getInstance() │       │
│ + getConnection()│      │
│ + initDatabase()│       │
│ + testConnection│       │
└─────────────────┘       │
                          │
    ┌─────────────────────┼─────────────────────┐
    │                     │                     │
┌───▼──────────┐    ┌────▼─────────┐
│ CandidateDAO │    │JobPostingDAO │
├──────────────┤    ├──────────────┤
│ + save()     │    │ + save()     │
│ + update()   │    │ + update()   │
│ + findById() │    │ + findById() │
│ + findAll()  │    │ + findAll()  │
│ + delete()   │    │ + delete()   │
│ + search()   │    │ + search()   │
└──────────────┘    └──────────────┘
```

### UI Layer
```
┌─────────────────┐
│ MainApplication │
├─────────────────┤
│ + start()       │
│ + stop()        │
│ + main()        │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│RecruiterDashboard│
├─────────────────┤
│ + start()       │
│ + createUI()    │
│ + loadData()    │
│ + uploadResume()│
│ + findMatches() │
│ + exportData()  │
└─────────────────┘
```

### Utility Layer
```
┌─────────────────┐    ┌─────────────────┐
│  ExcelExporter  │    │   RegexUtils    │
├─────────────────┤    ├─────────────────┤
│+exportCandidates│    │+extractEmail()  │
│+exportJobs()    │    │+extractPhone()  │
│+exportMatches() │    │+extractUrls()   │
│+exportShortlist │    │+extractDates()  │
└─────────────────┘    │+extractYears()  │
                       │+extractGPA()    │
                       │+extractDegrees()│
                       └─────────────────┘
```

## Key Design Patterns

### 1. Singleton Pattern
- `DatabaseManager` - Ensures single database connection instance

### 2. Data Access Object (DAO) Pattern
- `CandidateDAO`, `JobPostingDAO` - Encapsulate database operations

### 3. Strategy Pattern
- Parser implementations (`PDFParser`, `DOCXParser`, `TextParser`)

### 4. Factory Pattern
- `ResumeParser` delegates to appropriate parser based on file type

### 5. Observer Pattern
- JavaFX ObservableList for UI data binding

## Database Schema Relationships

```sql
candidates (1) ──── (M) candidate_skills
job_postings (1) ──── (M) job_skills
candidates (1) ──── (M) match_results (M) ──── (1) job_postings
```

## Component Dependencies

```
Main
 └── MainApplication
     └── RecruiterDashboard
         ├── CandidateDAO ──── DatabaseManager
         ├── JobPostingDAO ──── DatabaseManager
         ├── MatchingEngine
         ├── ResumeParser
         │   ├── PDFParser
         │   ├── DOCXParser
         │   └── TextParser
         ├── ExcelExporter
         └── RegexUtils
```

## Key Interfaces and Abstract Classes

### Parser Interface (Implicit)
```java
interface DocumentParser {
    String extractText(File file) throws IOException;
}
```

### DAO Interface (Implicit)
```java
interface GenericDAO<T, ID> {
    ID save(T entity);
    void update(T entity);
    T findById(ID id);
    List<T> findAll();
    boolean delete(ID id);
}
```

This architecture provides:
- **Separation of Concerns**: Each layer has distinct responsibilities
- **Loose Coupling**: Components interact through well-defined interfaces
- **High Cohesion**: Related functionality is grouped together
- **Extensibility**: Easy to add new parsers, matching algorithms, or UI components
- **Testability**: Each component can be tested independently
- **Maintainability**: Clear structure makes code easy to understand and modify