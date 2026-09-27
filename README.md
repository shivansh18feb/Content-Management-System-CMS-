# Production-Grade Developer Portfolio & Custom Headless CMS Platform

A full-stack, enterprise-ready developer portfolio and completely custom Content Management System (CMS) built from scratch using Java 21, Spring Boot 3, React, TypeScript, Tailwind CSS, PostgreSQL, and Docker.

---

## 🌟 System Architecture

```text
                                  INTERNET
                                     │
                    ┌────────────────┴────────────────┐
                    │                                 │
                    ▼                                 ▼
             PUBLIC PORTFOLIO                    CMS ADMIN
            React + Vite + TS                React + Vite + TS
               (Port 3000)                      (Port 5173)
                    │                                 │
                    └────────────────┬────────────────┘
                                     │
                                     ▼
                            REST API GATEWAY
                         Spring Boot 3 + Java 21
                               (Port 8080)
                                     │
           ┌─────────────────────────┼─────────────────────────┐
           │                         │                         │
           ▼                         ▼                         ▼
      Security / RBAC        Domain Services            Media & Assets
    JWT + Refresh Tokens     Projects, Blogs, Skills,   Multipart Upload &
   Rate Limiting & Audit      Experience, Education,    Local Storage Engine
           │                 Messages, Site Settings           │
           └─────────────────────────┼─────────────────────────┘
                                     │
                                     ▼
                          Spring Data JPA & Flyway
                                     │
                                     ▼
                           PostgreSQL 18 Database
```

---

## 🚀 Repositories & Modules

| Module | Technology Stack | Description |
| :--- | :--- | :--- |
| **`portfolio-backend`** | Spring Boot 3.3.4, Java 21, Spring Security, Spring Data JPA, Flyway, PostgreSQL | RESTful API, custom CMS engine, RBAC, JWT tokens, audit logging, media storage, rate limiting |
| **`portfolio-cms`** | React 19, Vite, TypeScript, Tailwind CSS, Lucide Icons, Axios | SaaS-grade CMS Admin Dashboard with full CRUD, draft/publish workflows, live analytics |
| **`portfolio-frontend`** | React 19, Vite, TypeScript, Tailwind CSS, Lucide Icons, Axios | Ultra-fast, SEO-optimized public portfolio website consuming real-time CMS APIs |
| **Docker** | Multi-stage Dockerfiles + Nginx reverse proxy + Alpine JRE | Production containerization for all 3 tiers with Postgres healthchecks |
| **CI/CD** | GitHub Actions (`.github/workflows/ci-cd.yml`) | Automated build, unit/integration testing, linting, and Docker container verification |

---

## 🛡️ Security Architecture

1. **Authentication & Authorization**:
   - Stateless JWT tokens (HMAC-SHA512) with 15-minute expiration.
   - Cryptographically random refresh tokens stored in PostgreSQL with 7-day expiration.
   - BCrypt password hashing with high work factor.
   - Role-Based Access Control (`ROLE_ADMIN`).
2. **Database & Persistence**:
   - Parameterized native queries with explicit `CAST(:param AS type)` ensuring zero SQL injection vulnerability and compatibility with modern PostgreSQL.
   - Automated schema migrations via Flyway with checksum validation.
3. **API Protection**:
   - IP-based rate limiting via Bucket4j/in-memory rate limiter on sensitive endpoints (`/api/auth/*`, `/api/public/contact`).
   - CORS origin whitelisting (`CORS_ALLOWED_ORIGINS`).
   - Global exception handling obscuring internal stack traces in production responses.
   - Comprehensive audit logging (`audit_logs` table) tracking every mutation with user ID, action, entity, IP address, and timestamp.
4. **Media Storage Security**:
   - Whitelist validation on uploaded MIME types (PNG, JPEG, WebP, SVG, GIF, PDF).
   - Maximum upload size strictly enforced at 15MB.
   - Files stored with random UUID filenames preventing directory traversal.

---

## 📡 REST API Reference

### Public Endpoints (`/api/public/*`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/public/about` | Bio, career summary, headline, location, and social links |
| `GET` | `/api/public/projects` | Paginated list of published projects with search and filter |
| `GET` | `/api/public/projects/featured` | Curated list of featured enterprise projects |
| `GET` | `/api/public/projects/{slug}` | Full project case study details by slug |
| `GET` | `/api/public/blogs` | Paginated list of published technical articles |
| `GET` | `/api/public/blogs/{slug}` | Full blog post content, category, and tags |
| `GET` | `/api/public/skills` | Categorized technical skills with proficiency ratings |
| `GET` | `/api/public/experience` | Work history and career timeline |
| `GET` | `/api/public/education` | Degrees, academic honors, and institutions |
| `GET` | `/api/public/services` | Engineering services and consulting offerings |
| `GET` | `/api/public/testimonials` | Client and peer endorsements |
| `GET` | `/api/public/social-links` | Verified social media profiles |
| `GET` | `/api/public/settings` | Public website title, SEO metadata, and contact info |
| `POST` | `/api/public/contact` | Public contact inquiry form submission with email dispatch |

### Admin Endpoints (`/api/admin/*` - Requires `ROLE_ADMIN` JWT)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/admin/dashboard/stats` | High-level system statistics (counts, views, unread inquiries) |
| `GET/PUT` | `/api/admin/about` | Read and update personal profile and bio |
| `GET/POST` | `/api/admin/projects` | List all projects (including drafts) or create a new project |
| `GET/PUT/DELETE` | `/api/admin/projects/{id}` | Read, update, or permanently delete project |
| `POST/PATCH` | `/api/admin/projects/{id}/publish` | Publish draft project to public portfolio |
| `POST/PATCH` | `/api/admin/projects/{id}/unpublish` | Revert project to draft status |
| `GET/POST` | `/api/admin/blogs` | List all articles or draft a new blog post |
| `POST/PATCH` | `/api/admin/blogs/{id}/publish` | Publish article to live public blog |
| `GET/POST/DELETE` | `/api/admin/skills` | Manage skills and skill categories |
| `GET/POST/DELETE` | `/api/admin/experience` | Manage career roles and timeline |
| `GET/POST/DELETE` | `/api/admin/education` | Manage educational background |
| `GET/POST/DELETE` | `/api/admin/services` | Manage consulting and engineering offerings |
| `GET/POST/DELETE` | `/api/admin/testimonials` | Manage and moderate endorsements |
| `POST` | `/api/admin/media` | Multipart upload for images and documents |
| `GET/DELETE` | `/api/admin/media` | List media library files or delete file |
| `GET/PATCH/DELETE`| `/api/admin/messages` | Read visitor inquiries, change status, or archive |
| `GET/PUT` | `/api/admin/settings` | Update site name, SEO tags, contact email |
| `GET` | `/api/admin/audit-logs` | Filtered, paginated security audit logs |

---

## 🛠 Quick Start (Local Development)

### Prerequisites
- **Java 21 LTS**
- **Apache Maven 3.9+**
- **Node.js v20+** and npm
- **PostgreSQL 16+** running on `localhost:5432`

### 1. Database Setup
```sql
CREATE DATABASE portfolio_cms_db;
```

### 2. Run Backend
```bash
cd portfolio-backend
mvn spring-boot:run
```
- API starts at: `http://localhost:8080`
- Swagger OpenAPI documentation: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

### 3. Run Custom CMS Admin Dashboard
```bash
cd portfolio-cms
npm install
npm run dev -- --port 5173
```
- CMS Admin: `http://localhost:5173`
- Default Admin Login: `admin@portfolio.com` / `Admin@123456`

### 4. Run Public Portfolio
```bash
cd portfolio-frontend
npm install
npm run dev -- --port 3000
```
- Public Portfolio: `http://localhost:3000`

---

## 🐳 Docker Deployment

The application includes production-ready multi-stage Dockerfiles for all 3 tiers with Nginx reverse proxying and automated healthchecks.

### Run with Docker Compose
```bash
# 1. Create environment file from template
cp .env.example .env

# 2. Build and launch all services
docker compose up --build -d

# 3. Check container status
docker compose ps
```

Services exposed:
- **Public Portfolio**: `http://localhost:3000`
- **CMS Admin**: `http://localhost:5173`
- **Spring Boot API**: `http://localhost:8080`
- **PostgreSQL**: `localhost:5432`

---

## 🧪 Testing & Quality Assurance

### Run Backend Unit & Integration Tests (20 Tests)
```bash
cd portfolio-backend
mvn test
```
Test suite includes:
- `AuthServiceTest` — Login, token generation, bad password rejection, token refresh.
- `ProjectServiceTest` — Project creation, validation, publishing, and public filtering.
- `BlogServiceTest` — Blog drafting, slug uniqueness, categorization, publish lifecycle.
- `ContactServiceTest` — Inquiry submission, email notification dispatch, audit logging.
- `SlugUtilsTest` — Normalization, accent stripping, URL character sanitization.

### Lint & Build Frontend Modules
```bash
# CMS Admin
cd portfolio-cms
npm run lint
npm run build

# Public Portfolio
cd portfolio-frontend
npm run lint
npm run build
```
Both frontends pass TypeScript compilation and lint checks with 0 errors.
