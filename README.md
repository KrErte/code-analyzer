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

- **Backend**: Java 21, Spring Boot 3.3, Gradle, WebFlux
- **Frontend**: Angular 17 (standalone components), TailwindCSS, Nginx
- **AI**: Anthropic Claude API (claude-sonnet-4-20250514)
- **Containerization**: Docker, Docker Compose

## Quick Start with Docker

The easiest way to run the application:

```bash
# 1. Clone the repository
git clone <repository-url>
cd code-analyzer

# 2. Set up environment variables
cp .env.example .env
# Edit .env and add your ANTHROPIC_API_KEY

# 3. Run with Docker Compose
docker compose up --build

# Application will be available at http://localhost
```

### Docker Commands

```bash
# Start in background
docker compose up -d --build

# View logs
docker compose logs -f

# Stop containers
docker compose down

# Rebuild without cache
docker compose build --no-cache

# Development mode (port 4200)
docker compose -f docker-compose.dev.yml up --build
```

## Manual Setup (Development)

### Prerequisites

- Java 21+
- Node.js 18+
- Gradle 8.7+ (or use wrapper)
- Anthropic API Key

### Backend Setup

```bash
cd backend

# Set your Anthropic API key
export ANTHROPIC_API_KEY=your_api_key_here

# Run with Gradle wrapper
./gradlew bootRun

# Or build and run jar
./gradlew bootJar
java -jar build/libs/code-analyzer.jar
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

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `ANTHROPIC_API_KEY` | Your Anthropic API key | Yes |
| `SPRING_PROFILES_ACTIVE` | Spring profile (docker) | No |

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
├── docker-compose.yml          # Production Docker setup
├── docker-compose.dev.yml      # Development Docker setup
├── .env.example                # Environment template
│
├── backend/
│   ├── Dockerfile
│   ├── build.gradle            # Gradle build config
│   ├── settings.gradle
│   └── src/main/java/com/codeanalyzer/
│       ├── controller/
│       │   ├── AnalysisController.java
│       │   └── GlobalExceptionHandler.java
│       ├── service/
│       │   ├── ClaudeService.java
│       │   └── PromptTemplateService.java
│       ├── dto/
│       │   ├── AnalysisRequest.java
│       │   ├── AnalysisResponse.java
│       │   └── Finding.java
│       ├── model/
│       │   ├── Persona.java
│       │   ├── Language.java
│       │   └── Severity.java
│       └── config/
│           ├── ClaudeConfig.java
│           └── CorsConfig.java
│
└── frontend/
    ├── Dockerfile
    ├── nginx.conf              # Nginx config with API proxy
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
