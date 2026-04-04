# 💼 Finance Dashboard — Backend API

A role-based finance management system built with **Spring Boot 4**, **PostgreSQL**, and **JWT Authentication**. The system allows organizations to manage financial records with strict access control based on user roles.

---

## 🔗 Links

| Resource | URL |
|---|---|
| 🐙 GitHub Repository | [finance-dashboard](https://github.com/TechTAnish-07/finance-dashboard) |
| 📊 System Design Diagram | [View Diagram](https://drive.google.com/file/d/1EX3IEqkmlPV5NjAp8P4rkq8aLpgM8Ca1/view?usp=sharing) |
| 📖 API Documentation | `{base-url}/swagger-ui/index.html` |
| 🚀 Live API | Deployed on Railway |

---

## 🧠 System Overview

This is the backend for a **Finance Dashboard System** where different users interact with financial records based on their role. The system supports multiple organizations, each with their own isolated data.

```
Super Admin (Platform Owner)
        ↓
Creates Organization + Admin
        ↓
Admin manages users & financial records within their org
        ↓
Analyst analyzes their own records & insights
        ↓
Viewer views their own records & basic dashboard
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| **Spring Boot 4.0.5** | Backend framework |
| **Spring Security + JWT** | Authentication & authorization |
| **Spring Data JPA + Hibernate** | ORM & database interaction |
| **PostgreSQL (Neon DB)** | Cloud database |
| **Brevo (Sendinblue)** | Email service |
| **Springdoc OpenAPI** | Swagger API documentation |
| **Lombok** | Boilerplate reduction |
| **Maven** | Build tool |
| **Railway** | Cloud deployment |

---

## 👥 Roles & Permissions

The system has **4 roles** with clearly defined permissions:

| Permission | SUPER_ADMIN | ADMIN | ANALYST | VIEWER |
|---|---|---|---|---|
| Create Organization | ✅ | ❌ | ❌ | ❌ |
| Create Admin | ✅ | ❌ | ❌ | ❌ |
| Create Analyst / Viewer | ❌ | ✅ | ❌ | ❌ |
| Manage user status | ❌ | ✅ | ❌ | ❌ |
| Create / Edit / Delete records | ❌ | ✅ | ❌ | ❌ |
| View all org records | ❌ | ✅ | ❌ | ❌ |
| Filter & search records | ❌ | ✅ | ✅ | ❌ |
| View analytics & trends | ❌ | ✅ | ✅ | ❌ |
| View own records | ❌ | ✅ | ✅ | ✅ |
| View basic dashboard | ❌ | ✅ | ✅ | ✅ |

### Role Descriptions

**🔴 SUPER_ADMIN** — Platform owner. Seeded on startup. Creates organizations and their first Admin.

**🟠 ADMIN** — Organization manager. Creates users (ANALYST/VIEWER), manages all financial records within their org.

**🟡 ANALYST** — Can view and analyze their own records. Has access to filters, search, and full analytics.

**🟢 VIEWER** — Read-only access to their own records and basic dashboard summary.

---

## 🔄 User Flow

### 1. Organization & Admin Creation
```
App starts → SUPER_ADMIN auto-created (seeded)
        ↓
SUPER_ADMIN calls POST /auth/create-admin
        ↓
System creates Organization + Admin user
        ↓
Admin receives email with temporary password
        ↓
Admin logs in → changes password on first login
```

### 2. User Creation (by Admin)
```
Admin calls POST /auth/create-user
        ↓
System creates ANALYST or VIEWER in same org as Admin
        ↓
New user receives email with temporary password
        ↓
User logs in → changes password on first login
```

### 3. Financial Record Flow
```
Admin creates record for a specific user (by email)
        ↓
Record stored with: user_id, created_by, org_id
        ↓
ADMIN → sees all org records
ANALYST → sees only own records (with filters)
VIEWER → sees only own records (no filters)
```

### 4. Dashboard Flow
```
All roles hit GET /dashboard/summary
        ↓
VIEWER → gets: total income, expenses, net balance, recent activity
ANALYST → gets: + category totals, monthly trends (own data)
ADMIN → gets: + category totals, monthly trends (entire org data)
```

---

## 🗄️ Database Schema

```
organizations
─────────────────────────────────
id              BIGINT PK
name            VARCHAR
status          ENUM (ACTIVE, INACTIVE)
created_date    TIMESTAMP
status_updated_date TIMESTAMP

users
─────────────────────────────────
id              BIGINT PK
name            VARCHAR NOT NULL
email           VARCHAR UNIQUE
password        VARCHAR (BCrypt hashed)
role            ENUM (SUPER_ADMIN, ADMIN, ANALYST, VIEWER)
status          ENUM (ACTIVE, INACTIVE)
is_first_login  BOOLEAN
org_id          FK → organizations
created_at      TIMESTAMP
updated_at      TIMESTAMP

financial_records
─────────────────────────────────
id              BIGINT PK
amount          DECIMAL
type            ENUM (INCOME, EXPENSE)
category        VARCHAR (SALARY, RENT, FOOD, etc.)
date            DATE
description     VARCHAR
is_deleted      BOOLEAN (soft delete)
user_id         FK → users (whose record)
created_by      FK → users (who created it)
org_id          FK → organizations
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

---

## 📋 API Endpoints

### 🔐 Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/login` | Public | Login, returns JWT token |
| POST | `/auth/create-admin` | SUPER_ADMIN | Create organization + admin |
| POST | `/auth/create-user` | ADMIN | Create analyst or viewer |
| PUT | `/auth/change-password` | All | Change own password |
| GET | `/auth/me` | All | Get logged-in user info |

### 💰 Financial Records
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/records/create-record` | ADMIN | Create record for a user |
| GET | `/records` | ADMIN, ANALYST, VIEWER | Get records (role-scoped) |
| PUT | `/records/{id}` | ADMIN | Update a record |
| DELETE | `/records/{id}` | ADMIN | Soft delete a record |

#### Query Parameters for `GET /records`
```
?type=INCOME or EXPENSE
?category=SALARY
?startDate=2024-01-01&endDate=2024-01-31
?search=keyword
?page=0&size=10
?sortBy=date&sortDir=desc
```

> ⚠️ VIEWER cannot use filters or search. ANALYST and ADMIN can.

### 📊 Dashboard
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/dashboard/summary` | All | Full summary (role-scoped) |

#### Dashboard Response by Role
```
VIEWER  → totalIncome, totalExpenses, netBalance, recentActivity
ANALYST → + categoryTotals, monthlyTrends (own data)
ADMIN   → + categoryTotals, monthlyTrends (org-wide data)
```

---

## 🏗️ Project Structure

```
src/
└── main/
    └── java/
        └── com/example/finance_dashboard/
            │
            ├── Config/                         # Configuration classes
            │   ├── AdminInitializer.java        # Seeds SUPER_ADMIN on startup
            │   ├── JwtAuthFilter.java           # JWT filter for every request
            │   ├── SecurityConfig.java          # Spring Security configuration
            │   ├── SwaggerConfig.java           # OpenAPI/Swagger setup
            │   └── TokenUTIL.java              # JWT utility
            │
            ├── Controller/                     # REST API endpoints
            │   ├── AuthController.java          # Login, register, password
            │   ├── FinancialRecordController.java # CRUD for records
            │   └── DashboardController.java     # Summary & analytics
            │
            ├── Service/                        # Business logic
            │   ├── AuthService.java             # Auth business logic
            │   ├── FinancialService.java        # Record business logic
            │   ├── DashboardService.java        # Analytics logic
            │   ├── JwtService.java             # JWT generation & validation
            │   ├── CustomUserDetailsService.java # Spring Security user loading
            │   └── BrevoEmailService.java       # Email sending via Brevo
            │
            ├── Repository/                     # Database access
            │   ├── UserRepo.java               # User queries
            │   ├── OrgRepo.java                # Organization queries
            │   └── FinanceRepo.java            # Financial record queries
            │
            ├── Entity/                         # JPA entities (DB tables)
            │   ├── User.java                   # Users table
            │   ├── Organizations.java          # Organizations table
            │   └── FinancialRecord.java        # Financial records table
            │
            ├── DTO/                            # Data transfer objects
            │   ├── auth/
            │   │   ├── LoginReq.java
            │   │   ├── LoginResponse.java
            │   │   ├── CreateAdminReq.java
            │   │   ├── CreateUserReq.java
            │   │   └── ChangePasswordReq.java
            │   ├── FinanceRecord/
            │   │   ├── CreateRecordReq.java
            │   │   └── FinancialRecordResponse.java
            │   ├── Dashboard/
            │   │   ├── DashboardSummaryResponse.java
            │   │   ├── CategoryTotalResponse.java
            │   │   └── MonthlyTrendResponse.java
            │   ├── Role.java                   # ENUM: SUPER_ADMIN, ADMIN, ANALYST, VIEWER
            │   └── Status.java                 # ENUM: ACTIVE, INACTIVE
            │
            └── Exception/                      # Error handling
                └── GlobalExceptionHandler.java  # @ControllerAdvice
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 21
- Maven
- PostgreSQL (or Neon DB account)
- Brevo account (for email)

### 1. Clone the Repository
```bash
git clone https://github.com/TechTAnish-07/finance-dashboard.git
cd finance-dashboard
```

### 2. Create `application.yml`
Copy the example and fill in your values:
```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

```yaml
server:
  port: ${PORT:8080}

app:
  frontend-url: ${FRONTEND_URL:http://localhost:3000}

spring:
  application:
    name: finance-dashboard
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION}

brevo:
  api-key: ${BREVO_API_KEY}

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

### 3. Set Environment Variables
```bash
export DB_URL=jdbc:postgresql://your-db-url/dbname?sslmode=require
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_super_secret_key_minimum_32_chars
export JWT_EXPIRATION=86400000
export BREVO_API_KEY=your_brevo_api_key
export FRONTEND_URL=http://localhost:3000
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

### 5. Access Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

---

## 🌱 Default Super Admin

On first startup, a **SUPER_ADMIN** is automatically created:

```
Email:    patidartanish31@gmail.com
Password: (configured in AdminInitializer)
Role:     SUPER_ADMIN
```

> ⚠️ Change the default password immediately after first login.

---

## 🔐 Authentication

The API uses **JWT Bearer Token** authentication.

### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "yourpassword"
}
```

### Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

### Use Token
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📧 Email System

When a new user is created, the system automatically sends a welcome email containing:
- Login credentials (email + temporary password)
- Assigned role
- Login link
- Warning to change password on first login

Emails are sent via **Brevo (Sendinblue)** SMTP API.

---

## 📌 Key Design Decisions & Assumptions

| Decision | Reasoning |
|---|---|
| **No self-registration** | Finance systems are internal — only admins create users |
| **Invite-based flow** | Admin creates user → system sends email with temp password |
| **Soft delete** | Records are marked `is_deleted=true` instead of removed — preserves audit trail |
| **Multi-tenancy** | Every query is scoped to `org_id` — organizations cannot see each other's data |
| **Two FK on records** | `user_id` = data ownership, `created_by` = audit trail |
| **Role-scoped queries** | Same endpoint returns different data based on role — enforced in service layer |
| **SUPER_ADMIN seeded** | First admin created on startup — standard pattern for internal systems |
| **Fixed categories** | ENUM categories ensure consistent grouping for analytics |

---

## 🛡️ Security

- Passwords hashed with **BCrypt**
- JWT tokens with expiration
- Role-based access via **Spring Security** + `@PreAuthorize`
- Org-level data isolation — cross-org access impossible
- Temporary passwords sent via email, never stored in plain text
- `@Valid` input validation on all request bodies
- Global exception handler for consistent error responses

---

## 📊 Error Response Format

```json
{
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Amount must be positive",
  "timestamp": "2024-01-01T10:00:00"
}
```

---

## 👨‍💻 Author

**Tanish Patidar**
- GitHub: [@TechTAnish-07](https://github.com/TechTAnish-07)
- Email: patidar29tanish@gmail.com
