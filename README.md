# MultimediaWebApp Backend

# Backend is deployed on AWS Ec2
http://13.206.241.248:8080/

Backend service for the MultimediaWebApp platform built using Spring Boot and PostgreSQL.

The backend handles:

* User authentication using JWT
* File upload and management
* AI-powered document summarization
* Question answering from uploaded files
* Audio transcription support
* Chat history management

---

# Tech Stack

* Java 21
* Spring Boot
* Spring Security
* PostgreSQL
* JWT Authentication
* Docker
* Maven

---

# Features

* User Registration and Login
* JWT Authentication
* Upload PDF, audio, and multimedia files
* AI-generated summaries
* Chat with uploaded documents
* Store chat history
* REST API architecture
* Dockerized deployment

---

# Project Structure

```text
src/main/java
├── controller
├── service
├── repository
├── entity
├── dto
├── security
├── config

src/main/resources
├── application.yml
```

---

# Prerequisites

Install the following before running the project:

* Java 21
* Maven
* PostgreSQL
* Docker (optional)

---

# Environment Variables

The application uses environment variables during deployment.

Example values:

```env
DB_URL=jdbc:postgresql://localhost:5432/MultimediaWebApp
DB_USERNAME=postgres
DB_PASSWORD=your_password

JWT_SECRET=your_secret

GROQ_CHAT_API_KEY=your_chat_key
GROQ_AUDIO_API_KEY=your_audio_key
```

---

# Local Setup

## Clone Repository

```bash
git clone <backend-repository-url>
```

## Move Into Project Directory

```bash
cd MultimediaWebApp
```

## Run Application

```bash
mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

---

# Build

Create executable JAR:

```bash
mvn clean package
```

Generated file:

```text
target/MultimediaWebApp.jar
```

---

# Docker Setup

## Build Docker Image

```bash
docker build -t multimedia-webapp .
```

## Run Docker Container

```bash
docker run -p 8080:8080 multimedia-webapp
```

---

# API Documentation

# Authentication APIs

## Register User

```http
POST /auth/register
```

Request Body:

```json
{
  "email": "user@gmail.com",
  "password": "password"
}
```

---

## Login User

```http
POST /auth/login
```

Request Body:

```json
{
  "email": "user@gmail.com",
  "password": "password"
}
```

---

# Document APIs

## Upload Document

```http
POST /api/documents/upload
```

Headers:

```http
Authorization: Bearer <token>
```

---

## Get All Documents

```http
GET /api/documents
```

---

## Summarize Document

```http
GET /api/documents/{id}/summarize
```

---

## Chat With Document

```http
POST /api/documents/{id}/chat
```

Request Body:

```json
{
  "question": "Summarize this file"
}
```

---

## Get Chat History

```http
GET /api/documents/{id}/history
```

---

# Testing

Run unit tests:

```bash
mvn test
```

Testing includes:

* Service layer testing
* Repository testing
* JWT authentication testing
* Mockito unit tests

---

# Deployment

Backend is deployed using Docker containers.

Supported deployment platforms:

* AWS EC2
* Render
* Railway

Production database:

* AWS RDS PostgreSQL

---

# Security

* Passwords are encrypted using BCrypt
* JWT used for authentication
* CORS configured for frontend access
* Protected APIs require token authentication

---

# Notes

* Uploaded files are stored inside the `uploads` directory
* Docker is used for consistent deployment
* PostgreSQL is used as the primary database
* API responses are returned in JSON format
* Authentication is required for protected endpoints

---

# Running the Complete Backend Flow

1. Start PostgreSQL database
2. Configure environment variables
3. Run Spring Boot application
4. Register/Login user
5. Upload documents or multimedia files
6. Generate summaries or ask questions
7. View stored chat history

---

# Recommended Tools

* IntelliJ IDEA
* Postman
* Docker Desktop
* pgAdmin
* Maven

---

# License

This project is intended for learning, development, and portfolio purposes.
