# Recruitment Jobs Platform

A full-featured recruitment/job portal built with Spring Boot, PostgreSQL, Thymeleaf, and Bootstrap.

## Features

### For Job Seekers
- Browse and search jobs
- Apply for jobs with cover letter
- Track application status
- Interview scheduling notifications
- Request interview reschedule

### For Recruiters
- Create and manage company profile
- Post jobs (requires admin approval)
- View and manage applications
- Schedule interviews
- Track candidate status

### For Admin
- Manage users (enable/disable/lock)
- Approve/reject job postings
- Manage job categories

### Security Features
- Role-based authentication (JOB_SEEKER, RECRUITER, ADMIN)
- Password encryption (BCrypt)
- Remember me functionality
- Auto-lock after 5 failed login attempts
- Custom login error messages

### Notifications
- In-app notification system
- Alerts for application status changes
- Interview scheduling notifications

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.x, Spring Security
- **Database**: PostgreSQL
- **Frontend**: Thymeleaf, Bootstrap 5
- **Build Tool**: Maven

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- PostgreSQL (or Docker)

### Setup

1. **Start PostgreSQL** (if using Docker):
```bash
docker run -d --name recruitment_db \
  -e POSTGRES_DB=recruitment \
  -e POSTGRES_USER=recruitment_user \
  -e POSTGRES_PASSWORD=recruitment_pass \
  -p 5433:5432 postgres:15
```

2. **Configure database** in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/recruitment
    username: recruitment_user
    password: recruitment_pass
```

3. **Build and run**:
```bash
mvn clean package
java -jar target/recruitment-job-1.0.0.jar
```

4. **Access the app**: http://localhost:8081

### Local Docker Test

If you already use Laravel Herd or another local web server on port `80`, use the local Docker stack instead of the production one:

```bash
docker compose -f docker-compose.local.yml up -d --build
```

Open:

```text
http://localhost:8080
```

This local stack includes:

- PostgreSQL
- Spring Boot app
- Nginx on port `8080`

### Default Users

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@jobportal.com | admin123 |
| Recruiter | recruiter@company.com | recruiter123 |
| Job Seeker | jobseeker@email.com | seeker123 |

## Project Structure

```
src/
├── main/
│   ├── java/com/recruitment/
│   │   ├── config/       # Security, Web config
│   │   ├── controller/   # Controllers
│   │   ├── dto/          # Data Transfer Objects
│   │   ├── model/        # Entity classes
│   │   ├── repository/   # JPA repositories
│   │   └── service/     # Business logic
│   └── resources/
│       ├── static/       # CSS, JS, images
│       └── templates/    # Thymeleaf templates
└── test/
```

## Configuration

- **Port**: Default is 8081 (change in `application.yml`)
- **Database**: Runs on port 5433 by default
- **File Upload**: Saves to `/uploads/` folder

## License

MIT License
