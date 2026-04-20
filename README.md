<h1 align="center">
  🛡️ Safe Backup Project
</h1>

<p align="center">
  <strong>A modern, secure, cloud-based Android application for backing up and restoring SMS messages and Contacts, powered by a robust Spring Boot backend.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android Badge" />
  <img src="https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin Badge" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white" alt="Jetpack Compose Badge" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot Badge" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL Badge" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker Badge" />
</p>

## 📖 Overview

**Safe Backup** is an end-to-end mobile and backend solution designed to keep your most important data (Contacts & SMS) safe. The project was built with a focus on **clean architecture**, **modern UI/UX**, and **secure data transmission**.

This project serves as a comprehensive showcase of modern mobile development (MVVM, Jetpack Compose) and backend engineering (REST REST APIs, Spring Security, Docker containerization).

## ✨ Key Features

### 📱 Mobile App (Android / Kotlin)
- **Modern UI:** Built entirely with **Jetpack Compose** and Material Design 3.
- **Secure Authentication:** App login and registration using JWT tokens.
- **Cloud Sync:** One-tap manual backups for Contacts and SMS.
- **Restore Capability:** Instantly download and restore contacts to the device.
- **Permissions Management:** Handled dynamically and smoothly using modern Android APIs.

### ⚙️ Backend (Spring Boot / Java)
- **Secure API:** Endpoints secured with Spring Security and stateless **JWT** (JSON Web Tokens).
- **Data Persistence:** Relational database management using **Hibernate/Spring Data JPA** and **PostgreSQL**.
- **Rate Limiting:** Built-in rate limit protection (Resilience4j) to prevent abuse.
- **Containerized:** Ready-to-go **Docker Compose** environment for frictionless deployment.
- **API Documentation:** Integrated Swagger UI / OpenAPI 3.

---

## 🛠️ Technology Stack

### Mobile (Frontend)
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose
- **Architecture:** MVVM (Model-View-ViewModel)
- **Networking:** Retrofit2, OkHttp3
- **Asynchrony:** Kotlin Coroutines & Flow

### Backend (Server)
- **Language:** Java
- **Framework:** Spring Boot 3
- **Security:** Spring Security & JWT
- **Database:** PostgreSQL
- **Build Tool:** Maven

### Infrastructure & DevOps
- **Containerization:** Docker & Docker Compose
- **Version Control:** Git & GitHub

---

## 🚀 Getting Started

### Prerequisites
Make sure you have the following installed:
- [Docker & Docker Compose](https://www.docker.com/)
- [Android Studio](https://developer.android.com/studio) (for running the mobile app)
- Java 17+

### 1. Running the Backend
The backend is fully containerized. You don't need to manually configure the database.

```bash
# Clone the repository
git clone https://github.com/mat-kmiec/safe-backup-project.git
cd safe-backup-project

# Start the PostgreSQL database and Spring Boot backend
docker-compose up -d --build
```
*The backend API will be available at `http://localhost:8080/api/v1/...`*
*Swagger UI (API Docs) is typically at `http://localhost:8080/swagger-ui/index.html`*

### 2. Running the Mobile App
1. Open up **Android Studio**.
2. Select `File -> Open` and navigate to the `safe-backup-project/mobile` folder.
3. Let Gradle sync and resolve project dependencies.
4. Set up an Android Emulator (or connect a physical device).
5. Ensure the base URL in your `RetrofitClient` points to your backend instance.
6. Hit **Run** (▶️).

---




