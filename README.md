# Employee Management System

A full-stack Employee Management System developed using Java, Spring Boot, MySQL, and React.

The application provides a centralized platform for managing employee-related activities such as authentication, employee information, attendance, leave management, tasks, notifications, and role-based access.

---

## Features

- User registration and login
- JWT-based authentication
- Role-based access control
- Employee information management
- Employee profile management
- Attendance management
- Leave application and management
- Task management
- Notifications
- Role-based dashboards
- Employee search and filtering
- RESTful APIs
- MySQL database integration
- Request validation
- Exception handling
- Responsive React frontend

---

## User Roles

The application supports the following roles:

- ADMIN
- HR
- MANAGER
- EMPLOYEE

Different roles have access to different features based on their responsibilities.

---

## Technology Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- Bean Validation
- Maven

### Database

- MySQL

### Frontend

- React
- JavaScript
- HTML
- CSS
- React Router
- Axios
- Vite

### Development Tools

- IntelliJ IDEA
- Visual Studio Code
- Postman
- MySQL Workbench
- Git
- GitHub

---

## Project Architecture

The project follows a layered architecture.

```text
React Frontend
      |
      | HTTP / REST API
      ↓
Spring Boot Backend
      |
      ├── Controller
      |
      ├── Service
      |
      ├── Repository
      |
      ↓
JPA / Hibernate
      |
      ↓
MySQL Database