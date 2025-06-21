# Sunbird Serve Need Microservice

A Spring Boot microservice for managing volunteer needs and service coordination in the Sunbird ecosystem.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Gradle 7.6 or higher

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd sunbird-serve-need
   ```

2. **Set up environment variables**
   ```bash
   cp env-template.txt .env
   # Edit .env with your database credentials
   ```

3. **Run the application**
   ```bash
   # Development mode
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   
   # Or using environment variable
   export SPRING_PROFILES_ACTIVE=dev
   ./gradlew bootRun
   ```

4. **Access the application**
   - API Base URL: `http://localhost:9000/api/v1/serve-need/`
   - Swagger UI: `http://localhost:9000/api/v1/serve-need/swagger-ui.html`

## 📋 Configuration

The application supports multiple environment profiles. See [CONFIGURATION.md](CONFIGURATION.md) for detailed configuration instructions.

### Environment Profiles
- **dev**: Development environment with debug logging
- **prod**: Production environment with optimized settings
- **test**: Testing environment with H2 in-memory database

### Environment Variables
Key environment variables:
- `DB_URL`: Database connection URL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `SPRING_PROFILES_ACTIVE`: Active Spring profile

## 🏗️ Architecture

The application follows a layered architecture:

```
src/main/java/com/sunbird/serve/need/
├── NeedService/           # Core need management
│   ├── controllers/       # REST API endpoints
│   ├── services/         # Business logic
│   └── repositories/     # Data access layer
├── DeliverableService/   # Need deliverables management
├── SearchAndDiscoveryService/  # Search and discovery features
├── config/              # Configuration classes
└── models/              # Domain models and DTOs
```

## 🔧 API Endpoints

### Need Management
- `POST /need/raise` - Create a new need
- `PUT /need/update/{needId}` - Update an existing need
- `PUT /need/status/{needId}` - Update need status

### Need Discovery
- `GET /need/{needId}` - Get need by ID
- `GET /need/` - Get needs by status with pagination
- `GET /need/need-type/{needTypeId}` - Get needs by type
- `GET /need/user/{userId}` - Get needs by user

### Deliverables
- `POST /need-deliverable/create` - Create need deliverable
- `PUT /need-deliverable/update/{id}` - Update deliverable
- `GET /need-deliverable/{needPlanId}` - Get deliverables by plan

## 🧪 Testing

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests with specific profile
./gradlew test --args='--spring.profiles.active=test'
```

### Test Coverage
The application uses H2 in-memory database for testing to ensure fast and isolated test execution.

## 🐳 Docker

### Building the Image
```bash
docker build -t sunbird-serve-need .
```

### Running with Docker
```bash
docker run -p 9000:9000 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/serve-need \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=password \
  sunbird-serve-need
```

## 📊 Monitoring

### Health Checks
- Health endpoint: `GET /actuator/health`
- Info endpoint: `GET /actuator/info`

### Logging
- Development: Console and file logging (`needLog-dev.log`)
- Production: File logging only (`needLog-prod.log`)
- Test: Console logging only

## 🔒 Security

### Development Mode
- ✅ **No authentication required** - Easy setup for contributors
- ✅ **Swagger UI accessible** - No login prompts
- ✅ **CORS enabled** - All origins allowed
- ✅ **Open access** - Perfect for development and testing

### Production Security
This is an **open-source project** designed for flexibility. For production deployment, see [SECURITY.md](SECURITY.md) for multiple security options:

- **API Gateway Security** (Recommended)
- **Reverse Proxy with Authentication**
- **Environment-Based Security**
- **JWT Authentication** (Advanced)

### Security Philosophy
- 🌟 **Transparency**: Security through openness
- 🔧 **Flexibility**: Multiple deployment options
- 📚 **Documentation**: Clear security guidance
- 🤝 **Community**: Easy contribution setup

## 🤝 Contributing

We welcome contributions! Please see our contributing guidelines:

1. **Fork the repository**
2. **Create a feature branch**
3. **Make your changes**
4. **Add tests**
5. **Submit a pull request**

### Development Setup
- No authentication barriers for easy development
- Comprehensive documentation
- Clear configuration examples
- Test coverage requirements

## 📝 License

This project is licensed under the terms specified in the LICENSE file.

## 🆘 Support

For issues and questions:
1. Check the [CONFIGURATION.md](CONFIGURATION.md) for common issues
2. Review the [SECURITY.md](SECURITY.md) for security guidance
3. Review the API documentation at `/swagger-ui.html`
4. Check the application logs for error details
5. Create a GitHub issue for bugs or feature requests

## 🌟 Why This Approach?

As an **open-source project**, we prioritize:

- **Easy Onboarding**: Contributors can start immediately
- **Flexibility**: Users choose their security approach
- **Transparency**: Security through openness
- **Documentation**: Clear guidance for all use cases
- **Community**: Welcoming environment for contributions

This approach follows open-source best practices while providing clear guidance for production deployments.