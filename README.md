# MailMind - AI Email Reply Generator

A full-stack application that generates AI-powered email replies using Google Gemini API.

## Architecture

    - Frontend: React + TypeScript + Tailwind CSS (Port 5173) — built with Vite
- Backend: Spring Boot + Java (Port 8080)
- AI: Google Gemini API

## Run locally

- Backend (from repo root or backend folder):
  - Ensure you have Maven installed (or use IDE run config).
  - Set env vars for the API (see Environment variables below).
  - From repo root: run Maven with the aggregator `pom.xml` (this avoids the “missing pom.xml” issue when running at the root). Or run in `backend/` directly.
- Frontend:
  - Install Node 20+.
  - Install dependencies and run the dev server. It proxies `/api` to `http://localhost:8080`.

## Environment variables

Backend reads API configuration from environment variables via `backend/src/main/resources/application.properties`:
- GEMINI_URL — Full Google Generative Language API endpoint, e.g.
  `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent`
- GEMINI_KEY — Your API key

Frontend (optional for production/static hosting):
- VITE_API_BASE_URL — Set to your backend base URL (e.g. `https://your-backend.example.com`). If omitted, frontend will call relative `/api`.

## Build & run with Docker

A multi-stage Dockerfile at the repo root builds the frontend, embeds it into the Spring Boot JAR under `static/`, and runs the backend.

- Build an image from the repo root.
- Run the container, passing `GEMINI_URL` and `GEMINI_KEY` env vars; the app listens on 8080.

## Deploy frontend to Vercel

The repository includes `vercel.json` that:
- Installs and builds the frontend from the `frontend/` subfolder
- Publishes the static assets from `frontend/dist/public`

On Vercel:
- Set the environment variable `VITE_API_BASE_URL` to your reachable backend base URL.
- Trigger a build; the previous error `../node_modules/.bin/vite: No such file or directory` is resolved by using local scripts.

## Deploy backend to Render (Docker)

Render supports deploying this repository as a Docker service using the provided `Dockerfile`.

- Create a new Web Service on Render and select “Docker” for Runtime.
- Connect this GitHub repo; Render will auto-detect `Dockerfile` and build it.
- Service settings:
  - Plan: pick any (512MB+ recommended)
  - Port: 8080
  - Start Command: leave blank (use image CMD). If you must override, use exec form: `java -jar /app/app.jar`.
  - Environment variables:
    - `GEMINI_URL`: e.g. `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent`
    - `GEMINI_KEY`: your API key
    - Optional: `JAVA_TOOL_OPTIONS` (e.g., `-XX:MaxRAMPercentage=75.0`)

Notes
- If you previously saw `sh: java: not found`, it was due to the runtime image/command. The Dockerfile now uses a Debian-based Eclipse Temurin JRE image and an exec-form CMD so `java` is present and executed directly.
- After the backend is live on Render, set `VITE_API_BASE_URL` on Vercel to the Render backend URL so the frontend calls the right API.

## What was fixed

- Backend reply generation was echoing the original email. Fixed by:
  - Stronger prompts instructing the model not to copy/quote the original.
  - A sanitizer that strips quoted/original blocks and lines mostly copied from the original.
  - Increased output token limits and explicit length guidance (short/medium/long) to get longer replies.
- Added backend unit tests for the sanitizer; test suite passes.
- Vercel build error fixed by updating `frontend/package.json` scripts to use local binaries and adding missing dependency `@radix-ui/react-tooltip`.
- Added root `pom.xml` aggregator so running Maven from repo root works (resolving the “asking for a pom.xml that is not part of this project” confusion).
- Added a multi-stage `Dockerfile` to produce a single deployable image.

## Troubleshooting

- Non-fast-forward push (git):
  - Your local branch is behind remote. Fetch, rebase onto `origin/main`, resolve conflicts, then push.
- Spring Boot cannot find `pom.xml`:
  - Use the new root aggregator `pom.xml` (run Maven at repo root), or run from `backend/`.
- Frontend can’t reach backend in production:
  - Set `VITE_API_BASE_URL` to your backend URL on the hosting platform.

## API

Endpoint: POST `/api/email/generate`

Request:
- emailContent: string (the original email)
- tone: string (e.g., professional, friendly)
- length: "short" | "medium" | "long" (optional)

Response: Plain text email reply.
