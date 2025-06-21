# Configuration Guide

This document explains how to configure the Sunbird Serve Need microservice for different environments.

## Environment-Specific Configuration

The application now supports multiple environment profiles:

- **dev**: Development environment
- **prod**: Production environment  
- **prod-local**: Local production testing environment
- **test**: Testing environment

## Setting Up Environment Variables

### 1. Copy the Environment Template

```bash
cp env-template.txt .env
```

### 2. Update the .env file with your values

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/serve-need
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password

# Active Profile
SPRING_PROFILES_ACTIVE=dev
```

## Running the Application

### Development Mode
```bash
# Using environment variables
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun

# Or using command line
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Production Mode
```bash
# Set production environment variables
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://prod-server:5432/serve-need
export DB_USERNAME=prod_user
export DB_PASSWORD=prod_password

./gradlew bootRun
```

### Local Production Testing
```bash
# Test production configuration locally
export SPRING_PROFILES_ACTIVE=prod-local
export DB_URL=jdbc:postgresql://ec2-35-154-40-146.ap-south-1.compute.amazonaws.com:5432/ev-vriddhi
export DB_USERNAME=postgres
export DB_PASSWORD=postgres123

./gradlew bootRun
```

### Testing Mode
```bash
# Run tests with test profile
./gradlew test --args='--spring.profiles.active=test'
```

## Configuration Files

### application.properties
Main configuration file with default values and environment variable placeholders.

### application-dev.properties
Development-specific settings:
- Debug logging enabled
- SQL queries shown
- Detailed error messages
- Local database connection
- All actuator endpoints exposed
- Permissive security settings

### application-prod.properties
Production-specific settings:
- Minimal logging
- No SQL queries shown
- No stack traces in errors
- Connection pooling optimized
- Environment variables for sensitive data
- Restricted actuator endpoints
- Enhanced security settings
- Swagger UI disabled

### application-prod-local.properties
Local production testing settings:
- Production-like configuration
- Swagger UI enabled for testing
- All actuator endpoints exposed
- Production database connection
- Production logging levels
- Production security settings

### application-test.properties
Test-specific settings:
- H2 in-memory database
- Minimal logging
- Create-drop schema
- H2 console enabled

## Security Configuration

### Development & Test Environments
- **No authentication required** for API endpoints
- **Swagger UI accessible** without login
- **CORS enabled** for all origins
- **H2 console accessible** in test mode

### Production Environment
- **Enhanced security** with restricted CORS
- **Authentication ready** (configure as needed)
- **Restricted actuator endpoints**
- **Secure headers** enabled
- **Swagger UI disabled**

### Local Production Testing
- **Production-like security** settings
- **Swagger UI enabled** for testing
- **All actuator endpoints** exposed
- **Production database** connection

### Accessing Swagger UI

#### Development/Test Mode
```bash
# No authentication required
http://localhost:9000/api/v1/serve-need/swagger-ui.html
```

#### Local Production Testing
```bash
# No authentication required (for testing)
http://localhost:9000/api/v1/serve-need/swagger-ui.html
```

#### Production Mode
```bash
# Swagger UI disabled in production
# Use API documentation or enable for debugging
```

### Troubleshooting Swagger Access

If you're still getting login prompts:

1. **Check your active profile**:
   ```bash
   echo $SPRING_PROFILES_ACTIVE
   ```

2. **Verify the application is running with correct profile**:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

3. **Check application logs** for security configuration:
   ```
   [INFO] c.s.s.n.config.SecurityConfig - Security configuration loaded for dev profile
   ```

4. **Clear browser cache** and try again

## Security Best Practices

### 1. Never commit sensitive data
- Database passwords
- API keys
- Secret tokens

### 2. Use environment variables in production
```bash
export DB_PASSWORD=your_secure_production_password
```

### 3. Use different databases for different environments
- Development: Local PostgreSQL
- Testing: H2 in-memory
- Production: Managed PostgreSQL

### 4. Security Configuration by Environment

#### Development
- Open access for easy development
- Debug information available
- All endpoints accessible

#### Local Production Testing
- Production-like settings
- Swagger UI enabled for testing
- All actuator endpoints exposed

#### Production
- Restricted CORS origins
- Authentication ready
- Minimal information exposure
- Secure headers
- Swagger UI disabled

## Docker Configuration

When running in Docker, pass environment variables:

```dockerfile
ENV SPRING_PROFILES_ACTIVE=prod
ENV DB_URL=jdbc:postgresql://db:5432/serve-need
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=secure_password
```

Or use docker-compose:

```yaml
version: '3.8'
services:
  app:
    build: .
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:postgresql://db:5432/serve-need
      - DB_USERNAME=postgres
      - DB_PASSWORD=${DB_PASSWORD}
```

## Monitoring & Health Checks

### Available Endpoints

#### Development Mode
- Health: `GET /actuator/health`
- Info: `GET /actuator/info`
- Metrics: `GET /actuator/metrics`
- All endpoints: `GET /actuator`

#### Local Production Testing
- Health: `GET /actuator/health`
- Info: `GET /actuator/info`
- Metrics: `GET /actuator/metrics`
- All endpoints: `GET /actuator`

#### Production Mode
- Health: `GET /actuator/health`
- Info: `GET /actuator/info`
- Other endpoints require authentication

### Health Check Example
```bash
curl http://localhost:9000/api/v1/serve-need/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    }
  }
}
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check if PostgreSQL is running
   - Verify database credentials
   - Ensure database exists

2. **Profile Not Active**
   - Check SPRING_PROFILES_ACTIVE environment variable
   - Verify profile-specific properties file exists

3. **Permission Denied**
   - Check file permissions for log files
   - Ensure application has write access to log directory

4. **Swagger UI Login Prompt**
   - Ensure you're running with dev/test profile
   - Check security configuration is loaded
   - Clear browser cache

5. **CORS Issues**
   - Check CORS configuration in SecurityConfig
   - Verify allowed origins in production

### Log Files

- Development: `needLog-dev.log`
- Local Production Test: `needLog-prod-local.log`
- Production: `needLog-prod.log`
- Test: No file logging (console only)

## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/serve-need` | Database connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `JPA_SHOW_SQL` | `false` | Show SQL queries in logs |
| `JPA_DDL_AUTO` | `validate` | Hibernate DDL strategy |
| `APP_LOG_LEVEL` | `INFO` | Application log level |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile | 