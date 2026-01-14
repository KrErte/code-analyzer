# AI Code Logic Analyzer

An AI-powered code review tool where Claude AI acts as a "senior developer" critically analyzing code for logic errors, edge cases, and potential bugs.

## Features

- **Three Review Personas**:
  - **Brutal Senior**: Harshly critical, finds every possible flaw
  - **Constructive Mentor**: Critical but educational, explains the "why"
  - **Edge Case Hunter**: Focuses specifically on boundary conditions

- **Multi-language Support**: Java, JavaScript, TypeScript, Python
- **Detailed Findings**: Categorized by severity (Critical/Warning/Suggestion)
- **Bug Likelihood Score**: 0-100 score indicating potential issues
- **Improved Code Suggestions**: Get a fixed version of your code
- **Session History**: Analysis history saved locally
- **Shareable Results**: Share analysis via unique URLs

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.3, WebFlux
- **Frontend**: Angular 17 (standalone components), TailwindCSS
- **AI**: Anthropic Claude API (claude-sonnet-4-20250514)

## Prerequisites

- Java 21+
- Node.js 18+
- Maven 3.8+
- Anthropic API Key

## Getting Started

### Backend Setup

```bash
cd backend

# Set your Anthropic API key
export ANTHROPIC_API_KEY=your_api_key_here

# Run the application
./mvnw spring-boot:run
```

The backend will start at `http://localhost:8080`

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

The frontend will start at `http://localhost:4200`

## API Endpoints

### Analyze Code
```
POST /api/analyze
Content-Type: application/json

{
  "code": "function example() { ... }",
  "language": "javascript",
  "context": "This function should validate user input",
  "persona": "brutal"
}
```

### Response
```json
{
  "id": "uuid",
  "score": 73,
  "summary": "This code has several critical issues...",
  "findings": [
    {
      "severity": "critical",
      "line": 15,
      "issue": "Null pointer possible",
      "explanation": "The variable could be null...",
      "suggestion": "Add a null check before..."
    }
  ],
  "improvedCode": "function example() { ... }",
  "analyzedAt": 1699999999999
}
```

### Other Endpoints
- `GET /api/health` - Health check
- `GET /api/personas` - List available personas
- `GET /api/languages` - List supported languages

## Configuration

### application.yml
```yaml
anthropic:
  api:
    key: ${ANTHROPIC_API_KEY}
    model: claude-sonnet-4-20250514
    max-tokens: 4096
```

## Project Structure

```
├── backend/
│   ├── src/main/java/com/codeanalyzer/
│   │   ├── controller/
│   │   │   ├── AnalysisController.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── service/
│   │   │   ├── ClaudeService.java
│   │   │   └── PromptTemplateService.java
│   │   ├── dto/
│   │   │   ├── AnalysisRequest.java
│   │   │   ├── AnalysisResponse.java
│   │   │   └── Finding.java
│   │   ├── model/
│   │   │   ├── Persona.java
│   │   │   ├── Language.java
│   │   │   └── Severity.java
│   │   └── config/
│   │       ├── ClaudeConfig.java
│   │       └── CorsConfig.java
│   └── src/main/resources/
│       └── application.yml
│
└── frontend/
    └── src/app/
        ├── components/
        │   ├── code-input/
        │   ├── persona-selector/
        │   ├── analysis-results/
        │   └── history-panel/
        ├── services/
        │   └── analysis.service.ts
        └── models/
            └── analysis.model.ts
```

## License

MIT
