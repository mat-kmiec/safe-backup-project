# Safe Backup Project

A full-stack application for backing up mobile data (Contacts and SMS) to a self-hosted cloud. I built this project to put my knowledge of modern Android development, backend architecture, and infrastructure into practice, serving as my portfolio project.

## Features

- **Authentication:** JWT-based login and registration with Spring Security. Added rate limiting (Resilience4j) on auth endpoints to prevent basic brute-force attacks.
- **Contacts Sync:** Backup and restore contacts directly from the device.
- **SMS Archive:** Backup SMS messages (restoring SMS is heavily restricted by modern Android API, so it acts more as an archive).
- **Managing Backups:** A native mobile dashboard to view, filter, and delete past backups.
- **Dockerized Backend:** The API and PostgreSQL database are configured via Docker Compose for easy deployment.

## Tech Stack

**Backend:**
- Java 17
- Spring Boot 3 & Spring Security
- PostgreSQL
- Libraries: Resilience4j (rate limiting), Swagger/OpenAPI (docs)
- Containerization: Docker & Docker Compose

**Mobile:**
- Kotlin
- Jetpack Compose (Material Design 3)
- Architecture: MVVM (Model-View-ViewModel)
- Networking: Retrofit & OkHttp3
- Concurrency: Kotlin Coroutines

## How to run locally

### 1. Backend API
The backend requires Docker to run the database and the Spring Boot application together.

```bash
git clone https://github.com/mat-kmiec/safe-backup-project.git
cd safe-backup-project

# Start both Postgres and the Spring Boot app
docker-compose up -d --build
```
Once it builds and starts, the API will be available at `http://localhost:8080`.
You can view the Swagger documentation at `http://localhost:8080/swagger-ui.html`.

### 2. Android App
1. Open the `/mobile` folder in Android Studio.
2. Let Gradle sync the project.
3. Run the application on an emulator or physical device (API 24+).
*Note: If you run this on a local emulator, ensure the Retrofit base URL in the app is set to `http://10.0.2.2:8080` (Android's alias for localhost).*

## Key Design Decisions

- **Jetpack Compose:** I chose Compose over XML to use the modern, declarative UI approach currently dominant in the Android ecosystem.
- **MVVM Architecture:** Strict separation between UI styling and business logic makes the Android codebase much cleaner and easier to test.
- **Validation & Security:** The backend implements strict regex and length validations on DTOs, coupled with rate limitations on the controllers to ensure stability and block simple script attacks.


