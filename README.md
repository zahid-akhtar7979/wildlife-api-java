# Wildlife Conservation Platform API

A comprehensive Spring Boot REST API for wildlife conservation content management with Keycloak authentication, built with production-quality standards.

## 🚀 Features

- **JWT Authentication** with Keycloak integration
- **Role-based Access Control** (Admin, Contributor)
- **Comprehensive Article Management** with rich content support
- **Advanced Search & Filtering** with full-text search capabilities
- **File Upload Support** with Cloudinary integration
- **Database Migrations** with Flyway
- **API Documentation** with OpenAPI/Swagger
- **Production-ready** logging, monitoring, and error handling
- **Caching** for improved performance
- **Comprehensive Testing** with TestContainers

## 🏗️ Architecture

### Technology Stack

- **Framework**: Spring Boot 3.2.2
- **Language**: Java 21
- **Database**: PostgreSQL with Hibernate/JPA
- **Authentication**: Keycloak with OAuth2/JWT
- **Documentation**: OpenAPI 3 (Swagger)
- **Database Migration**: Flyway
- **Object Mapping**: MapStruct
- **File Storage**: Cloudinary
- **Testing**: JUnit 5, TestContainers
- **Build Tool**: Maven

### Key Components

- **Entities**: User, Article with proper JPA relationships
- **DTOs**: Data Transfer Objects with validation
- **Services**: Business logic layer with transaction management
- **Controllers**: REST endpoints with comprehensive security
- **Security**: JWT authentication with role-based authorization
- **Repositories**: Spring Data JPA with custom queries

## 📋 Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 13+
- Keycloak 23+ (for authentication)
- Docker (optional, for containerized deployment)

## 🔧 Configuration

### Configuration Strategy

This project follows **enterprise-grade configuration management** with separate files per environment:

```
src/main/resources/
├── application.yml          # Base configuration
├── application-dev.yml      # Development environment  
├── application-test.yml     # Testing environment
├── application-staging.yml  # Staging environment
└── application-prod.yml     # Production environment
```

### Environment Variables

Set these environment variables for your target environment:

```bash
# Database Configuration
export DATABASE_URL="jdbc:postgresql://localhost:5432/wildlife"
export DATABASE_USERNAME="wildlife_user" 
export DATABASE_PASSWORD="your_password"

# Keycloak Configuration
export KEYCLOAK_ISSUER_URI="http://localhost:8080/realms/wildlife"
export KEYCLOAK_JWK_SET_URI="http://localhost:8080/realms/wildlife/protocol/openid-connect/certs"

# Application Settings
export SPRING_PROFILES_ACTIVE="dev"  # dev, test, staging, prod
export LOG_LEVEL="INFO"
export CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:3001"
```

> **Enterprise Approach**: Large companies like Amazon, Google, and Netflix use separate configuration files per environment rather than a single file with multiple profiles. This provides better security, maintainability, and deployment isolation. See [CONFIG-STRATEGY.md](./CONFIG-STRATEGY.md) for details.

### Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE wildlife;
CREATE USER wildlife_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE wildlife TO wildlife_user;
```

2. Database migrations will run automatically on startup via Flyway.

### Keycloak Setup

1. Create a new realm named `wildlife`
2. Create a client named `wildlife-api`
3. Configure client settings:
   - Client authentication: ON
   - Authorization: ON
   - Standard flow: ON
   - Service accounts roles: ON
4. Create roles: `ADMIN`, `CONTRIBUTOR`
5. Create users and assign appropriate roles

## 🚀 Getting Started

### Local Development

1. **Clone the repository**
```bash
git clone <repository-url>
cd wildlife-api-java
```

2. **Set environment variables**
```bash
# Set required environment variables
export DATABASE_URL="jdbc:postgresql://localhost:5432/wildlife"
export DATABASE_USERNAME="wildlife_user"
export DATABASE_PASSWORD="your_password"
export SPRING_PROFILES_ACTIVE="dev"
```

3. **Build the application**
```bash
mvn clean compile
```

4. **Run the application**
```bash
# Development mode with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or specify profile explicitly
mvn spring-boot:run -Dspring.profiles.active=dev
```

The application will start on `http://localhost:8080`

### Using Docker

1. **Build Docker image**
```bash
docker build -t wildlife-api .
```

2. **Run with Docker Compose**
```bash
docker-compose up -d
```

## 📚 API Documentation

### Swagger UI
Visit `http://localhost:8080/swagger-ui.html` for interactive API documentation.

### API Endpoints

#### Public Endpoints
- `GET /api/articles` - Get published articles
- `GET /api/articles/{id}` - Get article by ID
- `GET /api/articles/featured` - Get featured articles
- `GET /api/articles/categories` - Get all categories
- `GET /api/articles/tags` - Get all tags

#### Authenticated Endpoints
- `GET /api/users/me` - Get current user profile
- `PUT /api/users/me` - Update current user profile
- `POST /api/articles` - Create new article
- `PUT /api/articles/{id}` - Update article
- `DELETE /api/articles/{id}` - Delete article

#### Admin Only Endpoints
- `GET /api/users` - Get all users
- `POST /api/users/{id}/approve` - Approve user
- `GET /api/users/statistics` - Get user statistics
- `GET /api/articles/statistics` - Get article statistics

## 🔐 Authentication

### JWT Token Format
```bash
Authorization: Bearer <jwt-token>
```

### Required Claims
- `sub`: Keycloak user ID
- `email`: User email
- `preferred_username`: Username
- `realm_access.roles`: User roles

### Example Token Usage
```bash
curl -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIs..." \
     http://localhost:8080/api/users/me
```

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn test -Dtest=**/*IntegrationTest
```

### Test Coverage
```bash
mvn jacoco:report
# View report at target/site/jacoco/index.html
```

## 📊 Monitoring & Observability

### Actuator Endpoints
- `/actuator/health` - Application health check
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

### Logging
- Structured JSON logging in production
- Log levels configurable via environment variables
- Request/response logging for debugging

## 🔄 Database Schema

### Core Tables
- `users` - User accounts with Keycloak integration
- `articles` - Wildlife conservation articles
- `article_tags` - Article tagging system

### Key Features
- Automatic timestamp management
- Soft deletes where appropriate
- Comprehensive indexing for performance
- Foreign key constraints for data integrity

## 🚢 Deployment

### Production Checklist
- [ ] Set strong JWT secrets
- [ ] Configure HTTPS
- [ ] Set up database backups
- [ ] Configure monitoring and alerting
- [ ] Set appropriate log levels
- [ ] Configure CORS for production domains
- [ ] Set up database connection pooling
- [ ] Configure file upload limits

### Environment Profiles
- `dev` - Development with debug logging
- `test` - Testing with H2 in-memory database
- `prod` - Production with optimized settings

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

### Code Standards
- Follow Google Java Style Guide
- Write comprehensive tests
- Document public APIs
- Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the API documentation
- Review the logs for error details

## 🔗 Related Projects

- [Wildlife Frontend](../frontend) - React frontend application
- [Wildlife UI](../wildlife-ui) - Alternative React UI
- [Keycloak Configuration](./keycloak) - Keycloak setup scripts

---

**Built with ❤️ for Wildlife Conservation** 