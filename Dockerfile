# Multi-stage build: frontend + backend into a single Spring Boot image

# 1) Build the frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci --no-audit --no-fund
COPY frontend/ ./
# Produces frontend/dist/public
RUN npm run vercel-build

# 2) Build the backend jar and embed built frontend as static resources
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml ./
COPY backend/src ./src
# Copy built frontend assets into Spring Boot static resources
COPY --from=frontend-build /app/frontend/dist/public ./src/main/resources/static
RUN mvn -B -DskipTests package

# 3) Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV JAVA_OPTS=""
# Set your API config at runtime
ENV GEMINI_URL=""
ENV GEMINI_KEY=""
COPY --from=backend-build /app/backend/target/mailmind-sb-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
CMD ["sh", "-lc", "java $JAVA_OPTS -jar /app/app.jar"]

