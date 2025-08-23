# MailMind Spring Boot Backend

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Google Gemini API Key

### Configuration

1. **Get your Google Gemini API Key**:
   - Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Create or select a project
   - Generate an API key

2. **Update application.properties**:
   ```properties
   # Replace with your actual API key
   api.key=YOUR_GOOGLE_GEMINI_API_KEY_HERE
   ```

### Running the Backend

1. **Navigate to backend directory**:
   ```bash
   cd backend
   ```

2. **Run with Maven**:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or if you don't have the Maven wrapper:
   ```bash
   mvn spring-boot:run
   ```

3. **The backend will start on**: `http://localhost:8080`

### API Endpoint

- **POST** `/api/email/generate`
- **Request Body**:
  ```json
  {
    "emailContent": "Original email content here...",
    "tone": "professional"
  }
  ```
- **Response**: Generated email reply as plain text

### Available Tones
- `professional` (default)
- `friendly`
- `formal` 
- `casual`
- `enthusiastic`
- `concise`

### CORS Configuration
The backend is configured to accept requests from `http://localhost:5173` (the frontend).

### Troubleshooting

1. **Port 8080 already in use**:
   ```bash
   # Kill any process on port 8080
   lsof -ti:8080 | xargs kill -9
   ```

2. **API Key issues**:
   - Verify your Google Gemini API key is correct
   - Check that the key has proper permissions
   - Ensure you haven't exceeded rate limits

3. **CORS errors**:
   - Verify frontend is running on `http://localhost:5173`
   - Check that WebConfig.java allows the correct origin