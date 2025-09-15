# Media Management Tool

A full‑stack web application to manage media, categories, loans, and statistics. Built with Spring Boot (Java), MariaDB/MySQL, React (Vite), Redux Toolkit, and Material UI, secured with JWT and documented via Swagger/OpenAPI.

## Table of Contents
- Prerequisites
- Quick Start
- Configuration
- Project Structure
- Features
- Usage
- Demo
- Project Description
- Troubleshooting
- License

## Prerequisites
- Java 21+ and Maven
- Node.js 18+ and npm
- MariaDB or MySQL

## Quick Start
1) Clone the repository
```
git clone <your-repo-url>
cd <repo-folder>
```

2) Start the backend (Spring Boot)
```
cd backend
mvn clean install
mvn spring-boot:run
```
- Runs on `http://localhost:8080` by default.
- API docs: `http://localhost:8080/swagger-ui/index.html` (Springdoc OpenAPI).

3) Start the frontend (Vite + React)
```
cd ../frontend
npm install
npm run dev
```
- App runs on `http://localhost:5173`.

## Configuration

Backend environment is read from `src/main/resources/application.properties`, which expects environment variables for DB and mail. Set these before starting the backend.

- Required environment variables:
  - `DATASOURCE_URL` (e.g. `jdbc:mariadb://localhost:3306/media_management`)
  - `DATASOURCE_USER`
  - `DATASOURCE_PASSWORD`
  - `EMAIL_ADDRESS` (for notifications)
  - `EMAIL_PASSWORD`

Frontend expects a Google Books API key for ISBN lookup.

- Create a `.env` file in `frontend/`:
```
VITE_GOOGLE_API_KEY=your-google-books-api-key
```

## Project Structure
```
backend/
  src/
  pom.xml

frontend/
  src/
  package.json
  vite.config.js
```

## Features
- User authentication (login and registration)
- Media management with categories
- Loan management
- Dashboard with statistics
- QR/barcode scanning and ISBN lookup
- Email notifications for reminders

## Usage
- Backend: `mvn spring-boot:run` (port `8080`)
- Frontend: `npm run dev` (port `5173`)
- Open the app at `http://localhost:5173`

## Demo
Watch the demo video in the repo:
- `demo/demo(1).mp4`

## Project Description
### 1) Introduction and Goals
The tool manages media collections (books, movies, games, CDs, etc.) and supports tracking loans to always know where items are.

### 2) User Requirements Overview
Focus on ease of use and fast inputs. New media can be added manually or fetched via ISBN/barcode. Runs on typical Windows/Linux machines; optional mobile app integration is envisioned.

### 3) Functional Specifications
- GUI-based management of media, categories, favorites
- Users with password-based authentication, optional stay-logged-in
- Add, edit, delete media items; attach notes
- Import/fetch details via ISBN/barcode
- Manage loans (borrower, dates, due dates) and keep history with timelines
- Sort, filter, and search across the collection
- Optional statistics and smartphone integration (scan, notifications)

### 4) Non-functional Requirements
- Secure storage of user data and passwords
- Handles large datasets (10k media, 1k users, 10k loans)
- Responsive UI and fast DB operations

## Troubleshooting
- DB connection errors: verify `DATASOURCE_URL`, user, and password; ensure the DB exists and is reachable.
- CORS/auth issues: ensure the backend is running on `8080` and the frontend on `5173`. Default CORS allows both during development.
- Swagger not available: confirm the backend started without errors and visit `/swagger-ui/index.html` on port `8080`.

## License
Created by the AdamPos Team.
