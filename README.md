# MailMind - AI Email Reply Generator

A full-stack application that generates AI-powered email replies using Google Gemini API.

## Architecture

- **Frontend**: React + TypeScript + Tailwind CSS (Port 5173)
- **Backend**: Spring Boot + Java (Port 8080)
- **AI**: Google Gemini API

## Quick Start

### 1. Start the Backend (Spring Boot)

```bash
cd backend

# Configure your Google Gemini API key in src/main/resources/application.properties
# Replace: api.key=YOUR_GOOGLE_GEMINI_API_KEY_HERE

# Run the Spring Boot application
./mvnw spring-boot:run
```

Backend will be available at: `http://localhost:8080`

### 2. Start the Frontend (React)

In Replit, the frontend is already configured to run. The workflow "Start application" will start the React frontend on port 5173.

**For local development**:
```bash
npm install
npm run dev
```

Frontend will be available at: `http://localhost:5173`

## Project Structure

```
├── backend/                 # Spring Boot backend
│   ├── src/main/java/      # Java source files
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml             # Maven dependencies
├── client/                 # React frontend
│   └── src/               # Frontend source files
└── attached_assets/       # Original Spring Boot files
```

## Features

- ✅ AI-powered email reply generation
- ✅ Multiple tone options (professional, friendly, formal, casual, enthusiastic, concise)
- ✅ Clean, responsive UI with dark/light mode
- ✅ One-click copy functionality
- ✅ Real-time error handling
- ✅ CORS-enabled API

## API Usage

**Endpoint**: `POST http://localhost:8080/api/email/generate`

**Request**:
```json
{
  "emailContent": "Thank you for your inquiry about our services...",
  "tone": "professional"
}
```

**Response**: Plain text email reply

## Getting Google Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create or select a project  
3. Generate an API key
4. Add it to `backend/src/main/resources/application.properties`

## Local Development Setup

This project is configured to run with:
- Frontend on `http://localhost:5173` 
- Backend on `http://localhost:8080`

The frontend automatically connects to the Spring Boot backend API.