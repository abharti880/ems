
EMPLOYEE MANAGEMENT SYSTEM (EMS)
================================

OVERVIEW
--------
Employee Management System (EMS) is a Spring Boot based backend application that provides APIs
to manage employees, departments, leave requests, and notifications.
The system uses PostgreSQL as the database and RabbitMQ for asynchronous notifications.
The entire application is containerized using Docker and orchestrated via Docker Compose.

This is a backend-only service and is intended to be tested using Postman or curl.

--------------------------------------------------------------------
1. SETUP INSTRUCTIONS
--------------------------------------------------------------------

PREREQUISITES
- Java 17 or higher
- Maven 3.8 or higher
- Docker with Docker Compose v2
- Git

LOCAL SETUP (WITHOUT DOCKER)

1. Clone the repository
   git clone <repository-url>
   cd ems

2. Build the project
   mvn clean package

3. Ensure PostgreSQL and RabbitMQ are running locally

4. Run the application
   mvn spring-boot:run

Application URL:
http://localhost:8080

--------------------------------------------------------------------
DOCKER SETUP (RECOMMENDED)
--------------------------------------------------------------------

1. Build application JAR
   mvn clean package -DskipTests

2. Start services
   docker compose up -d --build

3. Verify running containers
   docker compose ps

4. Stop services
   docker compose down

Expected services:
- ems-app (Spring Boot application)
- ems-db (PostgreSQL)
- ems-rabbitmq (RabbitMQ)

Service URLs:
Application        : http://localhost:8080
RabbitMQ UI        : http://localhost:15672
RabbitMQ Username  : myuser
RabbitMQ Password  : secret

--------------------------------------------------------------------
2. API DOCUMENTATION
--------------------------------------------------------------------

AUTHENTICATION
HTTP Basic Authentication is used.
Authorization Header:
Authorization: Basic base64(username:password)

EMPLOYEE APIs
POST   /api/employees        -> Create new employee
GET    /api/employees        -> Get all employees
GET    /api/employees/me     -> Get logged-in employee

DEPARTMENT APIs
POST   /api/departments      -> Create department
GET    /api/departments      -> List departments

LEAVE APIs
POST   /api/leaves                     -> Submit leave request
GET    /api/leaves/employee/{empId}    -> Get leaves by employee
PUT    /api/leaves/{id}/status         -> Update leave status (ADMIN only)

Example Leave Request:
{
"employeeId": 2,
"startDate": "2026-03-20",
"endDate": "2026-03-22",
"reason": "Personal work"
}

--------------------------------------------------------------------
3. ARCHITECTURE OVERVIEW
--------------------------------------------------------------------

Client (Postman / Curl)
|
v
Spring Boot Application
|
+-- PostgreSQL (Employee, Department, Leave data)
|
+-- RabbitMQ
|
v
Notification Consumer (Simulated Email)

Key Design Points:
- Layered architecture (Controller, Service, Repository)
- DTO-based API responses
- Asynchronous notifications using RabbitMQ
- Dockerized deployment

--------------------------------------------------------------------
4. ASSUMPTIONS
--------------------------------------------------------------------

- Backend-only application (no UI)
- Email sending is simulated via logs
- Leave approvals are done by ADMIN users only
- Hibernate ddl-auto is set to update for simplicity
- Flyway migrations are disabled
- RabbitMQ queues/exchanges are auto-created
- Docker is the preferred runtime environment

--------------------------------------------------------------------
5. TESTING INSTRUCTIONS
--------------------------------------------------------------------

UNIT TESTS
- Service layer tests implemented
- External dependencies mocked
- Minimum 70% code coverage achieved

Run tests:
mvn test

API TESTING
- Import provided Postman collection
- Collection contains all endpoints with sample data

HEALTH CHECK (if actuator enabled)
GET /actuator/health

Expected Response:
{
"status": "UP"
}

--------------------------------------------------------------------
END OF DOCUMENT
--------------------------------------------------------------------