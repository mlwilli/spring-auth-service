# Spring Auth Service

A lightweight Spring Boot authentication + authorization service built with JWT, H2, and Spring Security.  
It includes user management, role-based permission checks, and a seeded test database for quick experimentation.

---

## Features

- **JWT authentication**
- **Role-based authorization** (`ROLE_ADMIN`, `ROLE_USER`)
- **User CRUD endpoints** (admin-only)
- **Password hashing** using BCrypt
- **H2 in-memory database** with auto-created tables
- **Seeded demo users** for easy testing
- Fully stateless API (no HTTP sessions)
- Integration test + GitHub Actions CI

---

## Demo Users

Automatically created on startup:

| Username | Password | Roles                 |
|----------|----------|----------------------|
| admin    | admin123 | ROLE_ADMIN, ROLE_USER|
| user     | user123  | ROLE_USER            |

---

## Running the App

Using Maven wrapper (recommended):

```bash
./mvnw spring-boot:run
Or from IntelliJ: run AuthServiceApplication.

H2 console (for local debugging):

text
Copy code
http://localhost:8080/h2-console
JDBC URL (default):

text
Copy code
jdbc:h2:mem:authdb
Authentication Flow
1. Login → Get JWT
Request

http
Copy code
POST /api/auth/login
Content-Type: application/json
json
Copy code
{
  "username": "admin",
  "password": "admin123"
}
Response

json
Copy code
{
  "token": "<jwt-here>",
  "tokenType": "Bearer",
  "username": "admin",
  "roles": ["ROLE_ADMIN", "ROLE_USER"]
}
Include the token on subsequent calls:

http
Copy code
Authorization: Bearer <jwt-here>
User Management (Admin Only)
All of the following require an Authorization: Bearer <token> header from an admin user.

List users
http
Copy code
GET /api/users
Get a user
http
Copy code
GET /api/users/{id}
Create user
http
Copy code
POST /api/users
Content-Type: application/json
json
Copy code
{
  "username": "newuser",
  "password": "password123",
  "roles": ["ROLE_USER"]
}
Update user
http
Copy code
PUT /api/users/{id}
Content-Type: application/json
json
Copy code
{
  "username": "updateduser",
  "password": "newpassword123",
  "roles": ["ROLE_USER"]
}
Enable / disable user
http
Copy code
PATCH /api/users/{id}/enabled?enabled=false
Delete user
h
Copy code
DELETE /api/users/{id}
Tests
There is an integration test that:

Logs in as the admin demo user

Retrieves a JWT

Calls the protected /api/users endpoint with the token

Verifies access is granted

Also verifies unauthenticated access is rejected

Run tests with:

bash
Copy code
./mvnw test
CI (GitHub Actions)
A minimal CI workflow is included at:

text
Copy code
.github/workflows/ci.yml
It runs on push and pull_request and executes:

bash
Copy code
mvn -B verify
using JDK 21.

Postman Collection
A sample Postman collection is provided in:

text
Copy code
postman-collection.json
It contains:

Login (admin) – calls /api/auth/login

List users – calls /api/users using a {{token}} variable

Import it into Postman, log in as admin, copy the returned JWT into the token variable, and you’re ready to hit protected endpoints.

Project Structure
text
Copy code
src/main/java/com/github/mlwilli/authservice
 ├─ AuthServiceApplication.java
 ├─ domain/      → User, Role
 ├─ repository/  → JPA repositories
 ├─ security/    → JWT service, filter, security config
 ├─ api/         → Controllers + DTOs
 └─ DataInitializer.java  → Seeds demo users and roles

src/test/java/com/github/mlwilli/authservice/api
 └─ AuthFlowIntegrationTest.java
