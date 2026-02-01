# User Authentication Backend

Spring Boot backend service for the User Authentication System.

## Features

- User registration and authentication
- JWT-based security
- Evaluation model management
- Competition and rating system
- Fruit nutrition query system
- File upload support
- Redis caching
- MySQL database with Flyway migrations

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access
- **MySQL 8.0** - Primary database
- **Redis 7.0** - Caching and session storage
- **Flyway** - Database migrations
- **JWT** - Token-based authentication
- **Maven** - Build tool
- **JUnit 5** - Unit testing
- **jqwik** - Property-based testing
- **TestContainers** - Integration testing

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker and Docker Compose

### Development Setup

1. **Start development environment:**
   ```bash
   # Windows
   scripts\dev-start.bat
   
   # Linux/Mac
   ./scripts/dev-start.sh
   ```

2. **Build and run locally:**
   ```bash
   mvn clean install
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Access the application:**
   - API: http://localhost:8080/api
   - Health: http://localhost:8080/api/health
   - Actuator: http://localhost:8080/actuator

### Testing

```bash
# Unit tests
mvn test

# Integration tests
mvn verify -P integration-test

# All tests with coverage
mvn clean verify jacoco:report

# Property-based tests (included in unit tests)
mvn test -Dtest=**/*Property
```

### Database Management

```bash
# Run migrations
mvn flyway:migrate

# Clean database (development only)
mvn flyway:clean

# Validate migrations
mvn flyway:validate

# Show migration info
mvn flyway:info
```

## API Documentation

### Authentication Endpoints

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Fruit Query Endpoints

- `GET /api/fruit/query?type={nutrition|flavor}&fruit={mango|banana}` - Query fruit data

### Health Endpoints

- `GET /api/health` - Basic health check
- `GET /actuator/health` - Detailed health information

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:mysql://localhost:3306/userauth_dev` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `userauth` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `password` |
| `SPRING_REDIS_HOST` | Redis host | `localhost` |
| `SPRING_REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | JWT signing secret | (required in production) |
| `JWT_EXPIRATION` | JWT expiration time (ms) | `86400000` (24h) |
| `UPLOAD_PATH` | File upload directory | `./uploads` |
| `MAX_FILE_SIZE` | Maximum file size | `10MB` |

### Profiles

- **dev** - Development environment with debug logging
- **test** - Test environment with test database
- **prod** - Production environment with optimized settings

## Security

### Authentication

- JWT-based stateless authentication
- BCrypt password hashing (strength 12)
- Token expiration and validation

### Authorization

- Role-based access control (ADMIN, USER)
- Method-level security annotations
- CORS configuration for cross-origin requests

### File Upload Security

- File type validation (JPG, PNG, WEBP)
- File size limits (10MB max)
- Secure file storage with unique names

## Database Schema

### Core Tables

- `users` - User accounts and roles
- `fruits` - Fruit master data
- `nutrition_data` - Fruit nutrition information
- `flavor_data` - Fruit flavor information

### Migration Strategy

- Flyway for version-controlled migrations
- Incremental schema updates
- Rollback support for development

## Monitoring and Observability

### Health Checks

- Database connectivity
- Redis connectivity
- Application status
- Custom health indicators

### Metrics

- JVM metrics
- HTTP request metrics
- Database connection pool metrics
- Custom business metrics

### Logging

- Structured logging with JSON format (production)
- Configurable log levels per package
- Request/response logging for debugging
- Security event logging

## Deployment

### Docker

```bash
# Build image
docker build -t userauth-backend .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/userauth \
  userauth-backend
```

### Production Checklist

- [ ] Set strong JWT secret
- [ ] Configure production database
- [ ] Set up Redis cluster
- [ ] Configure SSL/TLS
- [ ] Set up monitoring
- [ ] Configure log aggregation
- [ ] Set up backup strategy
- [ ] Configure firewall rules

## Development Guidelines

### Code Style

- Google Java Style Guide
- Use Spotless for formatting: `mvn spotless:apply`
- Follow Spring Boot best practices

### Testing Strategy

- Unit tests for service layer logic
- Integration tests for API endpoints
- Property-based tests for business rules
- TestContainers for database integration

### Git Workflow

- Feature branches from `develop`
- Pull requests for code review
- Automated CI/CD pipeline
- Semantic versioning for releases

## Troubleshooting

### Common Issues

**Database Connection Failed**
```bash
# Check MySQL service
docker-compose -f docker-compose.dev.yml ps mysql

# Check connection
mysql -h localhost -u userauth -p userauth_dev
```

**Redis Connection Failed**
```bash
# Check Redis service
docker-compose -f docker-compose.dev.yml ps redis

# Test connection
redis-cli -h localhost ping
```

**JWT Token Invalid**
- Check JWT secret configuration
- Verify token expiration settings
- Check system clock synchronization

### Performance Tuning

**Database**
- Optimize connection pool settings
- Add appropriate indexes
- Monitor slow queries

**Redis**
- Configure memory limits
- Set appropriate eviction policies
- Monitor cache hit rates

**JVM**
- Tune heap size for workload
- Configure garbage collection
- Monitor memory usage

## Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License.