# Task Management System

A **Scalable REST API** with JWT Authentication, Role-Based Access Control, and a fully functional frontend UI — built with Spring Boot 3, MySQL, and Vanilla JS.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.2.3 |
| Security | Spring Security, JWT (JJWT 0.12.6) |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| API Docs | Springdoc OpenAPI 3 (Swagger UI) |
| Frontend | Vanilla JS, HTML5, CSS3 |
| DevOps | Docker, Docker Compose |
| Build | Maven 3.9 |

---

## Features

### Authentication & Authorization
- User registration and login with **BCrypt password hashing**
- **JWT-based stateless authentication** (24-hour token expiry)
- **Role-based access control** — `USER` and `ADMIN` roles
- Admin users can view and manage all tasks; regular users see only their own

### Task Management (CRUD)
- Create, read, update, and delete tasks
- Task statuses: `PENDING`, `IN_PROGRESS`, `COMPLETED`
- Admin-only endpoint to fetch tasks by any user ID
- Full input validation with meaningful error messages

### Security & Scalability
- Stateless JWT authentication (no sessions)
- Global exception handling with structured error responses
- API versioning via `/api/v1/` prefix
- CORS configured for cross-origin support
- Docker + Docker Compose for container deployment
- Multi-stage Docker build for optimized image size
- Non-root container user for security

### API Documentation
- Swagger UI available at `/swagger-ui.html`
- Bearer token authentication integrated in Swagger
- All endpoints documented with descriptions

---

## Project Structure

```
src/
├── main/
│   ├── java/com/assignment/TaskManagementSystem/
│   │   ├── config/          # Security, CORS, OpenAPI configuration
│   │   ├── controller/      # REST controllers (Auth, Task)
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entity/          # JPA entities (User, Task)
│   │   ├── exception/       # Global exception handling
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── security/        # JWT filter, JWT service, UserDetailsService
│   │   └── service/         # Business logic (Auth, Task)
│   └── resources/
│       ├── application.properties
│       └── static/
│           └── index.html   # Frontend UI
```

---

## API Endpoints

### Auth Endpoints (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register a new user |
| POST | `/api/v1/auth/login` | Login and receive JWT token |

### Task Endpoints (JWT Required)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/v1/tasks` | USER, ADMIN | Create a new task |
| GET | `/api/v1/tasks` | USER, ADMIN | Get all tasks (admins see all, users see own) |
| GET | `/api/v1/tasks/{id}` | USER, ADMIN | Get task by ID |
| PUT | `/api/v1/tasks/{id}` | USER, ADMIN | Update a task |
| DELETE | `/api/v1/tasks/{id}` | USER, ADMIN | Delete a task |
| GET | `/api/v1/tasks/user/{userId}` | ADMIN only | Get tasks by user ID |

---

## Database Schema

```sql
-- users table
CREATE TABLE users (
    id         BINARY(16)   PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('USER', 'ADMIN') NOT NULL,
    created_at DATETIME     NOT NULL
);

-- tasks table
CREATE TABLE tasks (
    id          BINARY(16)   PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    status      ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED') NOT NULL,
    user_id     BINARY(16)   NOT NULL,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

## Setup & Run

### Prerequisites
- Java 21+
- MySQL 8.0+
- Maven 3.9+

### Local Setup

**1. Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/task-management-system.git
cd task-management-system
```

**2. Configure the database**

Create a MySQL database:
```sql
CREATE DATABASE task_management_db;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_management_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

**3. Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

---

### Docker Setup (Recommended)

Run the full stack (app + MySQL) with one command:

```bash
docker-compose up --build
```

The application will be available at **http://localhost:8080**

---

## Usage

### Frontend UI
Open **http://localhost:8080** in your browser to access the full Task Management UI.

### Swagger API Docs
Open **http://localhost:8080/swagger-ui.html** to explore and test all APIs.

### Sample API Requests

**Register:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com", "password": "secret123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john@example.com", "password": "secret123"}'
```

**Create Task (with JWT):**
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "My Task", "description": "Task description", "status": "PENDING"}'
```

---

## Scalability Notes

This project is designed with scalability in mind:

- **Stateless JWT** — servers share no session state, enabling horizontal scaling behind a load balancer
- **Modular structure** — each feature (auth, tasks) is independently layered (controller → service → repository), making it easy to extract into microservices
- **Docker-ready** — containerized for easy deployment on Kubernetes or any cloud platform (AWS ECS, GCP Cloud Run)
- **Database-agnostic** — the JPA abstraction layer allows switching from MySQL to PostgreSQL with minimal changes
- **API versioning** — `/api/v1/` prefix allows introducing new API versions without breaking existing clients
- **Future enhancements ready for**: Redis caching for frequent reads, message queues (Kafka/RabbitMQ) for async task notifications, and API Gateway for rate limiting

---

## Security Practices

- Passwords hashed with **BCrypt** (strength 10)
- JWT signed with **HMAC-SHA256**
- Stateless authentication — no server-side session storage
- Input validation on all endpoints using **Jakarta Bean Validation**
- Structured error responses — no stack traces exposed to clients
- Non-root Docker container user
- SQL injection prevented via JPA parameterized queries
