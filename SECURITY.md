# Security Configuration Guide

## Overview

This document outlines the security configuration for the Sunbird Serve Need microservice across different environments.

## Security Profiles

### 1. Development Profile (`dev`)
- **Swagger UI**: ✅ Enabled, no authentication
- **API Endpoints**: ✅ Open access
- **CORS**: ✅ Permissive (all origins allowed)
- **Actuator**: ✅ All endpoints exposed
- **Use Case**: Local development and testing

### 2. Production Local Profile (`prod-local`)
- **Swagger UI**: ✅ Enabled, no authentication
- **API Endpoints**: ✅ Open access
- **CORS**: ✅ Permissive (all origins allowed)
- **Actuator**: ✅ All endpoints exposed
- **Use Case**: Local testing with production settings

### 3. Production Profile (`prod`)
- **Swagger UI**: ✅ Enabled, **authentication required**
- **API Endpoints**: ✅ **Authentication required**
- **CORS**: 🔒 Restricted origins only
- **Actuator**: 🔒 Limited endpoints, authentication required
- **Use Case**: Production deployment

### 4. Basic Auth Profile (`basic-auth`)
- **Swagger UI**: ✅ Enabled, basic authentication
- **API Endpoints**: ✅ Basic authentication
- **CORS**: 🔒 Restricted origins
- **Actuator**: 🔒 Limited endpoints, authentication required
- **Use Case**: Simple authentication for development/testing

## Authentication Methods

### HTTP Basic Authentication (Production)
When using the `prod` profile, the application uses HTTP Basic Authentication:

```bash
# Example curl request with authentication
curl -u username:password \
  -H "Content-Type: application/json" \
  http://localhost:8080/api/needs

# For Swagger UI, enter credentials when prompted
```

### Configuration
To set up authentication credentials, add these environment variables:

```bash
# Set these in your production environment
SPRING_SECURITY_USER_NAME=your-username
SPRING_SECURITY_USER_PASSWORD=your-secure-password
```

## CORS Configuration

### Development/Testing
- All origins allowed
- All methods allowed
- All headers allowed

### Production
- Restricted to specific domains
- Specific HTTP methods only
- Specific headers only

## Security Best Practices

### 1. Environment Variables
- Never commit credentials to version control
- Use environment variables for sensitive data
- Use different credentials for each environment

### 2. CORS Configuration
- Restrict origins in production
- Only allow necessary HTTP methods
- Validate headers

### 3. Authentication
- Use strong passwords
- Consider implementing JWT for stateless authentication
- Implement rate limiting for API endpoints

### 4. Monitoring
- Monitor authentication attempts
- Log security events
- Use Spring Boot Actuator for health checks

## Open Source Security Strategy

### Philosophy
- **Easy Onboarding**: Minimal friction for contributors
- **Flexibility**: Multiple authentication options
- **Transparency**: Clear security documentation
- **Documentation**: Comprehensive guides

### Implementation
1. **Profile-based Security**: Different security levels for different environments
2. **Optional Authentication**: Can be enabled/disabled per environment
3. **Clear Documentation**: This guide and inline comments
4. **Easy Testing**: Local profiles for development

## Deployment Recommendations

### Development
```bash
# Use dev profile for local development
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Testing
```bash
# Use prod-local profile for production-like testing
./gradlew bootRun --args='--spring.profiles.active=prod-local'
```

### Production
```bash
# Use prod profile with authentication
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## Security Checklist

- [ ] Environment variables configured
- [ ] CORS origins restricted in production
- [ ] Authentication enabled in production
- [ ] Actuator endpoints secured
- [ ] Logging configured for security events
- [ ] Database credentials externalized
- [ ] HTTPS enabled in production
- [ ] Rate limiting implemented (optional)
- [ ] Security headers configured (optional)

## Troubleshooting

### Swagger UI Authentication Issues
1. Ensure you're using the correct profile
2. Check if authentication is enabled for your profile
3. Verify credentials are set correctly
4. Check browser console for CORS errors

### API Authentication Issues
1. Verify authentication is required for your profile
2. Check request headers for Authorization
3. Validate credentials
4. Check CORS configuration

### CORS Issues
1. Verify allowed origins in production
2. Check HTTP methods are allowed
3. Validate request headers
4. Test with different browsers

## Future Enhancements

### JWT Authentication
```java
// Example JWT configuration (future implementation)
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {
    // JWT token validation
    // Stateless authentication
    // Token refresh mechanism
}
```

### OAuth2 Integration
```java
// Example OAuth2 configuration (future implementation)
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    // OAuth2 provider configuration
    // Social login integration
    // Role-based access control
}
```

### API Gateway Integration
```yaml
# Example API Gateway configuration (future implementation)
spring:
  cloud:
    gateway:
      routes:
        - id: need-service
          uri: lb://need-service
          predicates:
            - Path=/api/**
          filters:
            - name: RequestRateLimiter
            - name: CircuitBreaker
```

## Support

For security-related issues or questions:
1. Check this documentation
2. Review inline code comments
3. Check Spring Security documentation
4. Create an issue in the project repository

## 🎯 **Quick Start Security Options**

### **For Internal Tools (No Auth)**
```bash
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

### **For Simple Production (Basic Auth)**
```bash
export SPRING_PROFILES_ACTIVE=basic-auth
export BASIC_AUTH_USERNAME=admin
export BASIC_AUTH_PASSWORD=secure_password
./gradlew bootRun
```

### **For Enterprise (API Gateway)**
```bash
export SPRING_PROFILES_ACTIVE=prod
# Configure API Gateway separately
./gradlew bootRun
```

## 🛡️ **Security Best Practices**

### **1. Network Security**
- ✅ Use HTTPS in production
- ✅ Implement proper firewall rules
- ✅ Use private networks for database access
- ✅ Regular security updates

### **2. Application Security**
- ✅ Input validation (implement Bean Validation)
- ✅ SQL injection prevention (use JPA/Hibernate)
- ✅ XSS protection (configure security headers)
- ✅ CSRF protection (enable for web forms)

### **3. Data Security**
- ✅ Encrypt sensitive data at rest
- ✅ Use environment variables for secrets
- ✅ Regular database backups
- ✅ Data retention policies

### **4. Monitoring & Logging**
- ✅ Security event logging
- ✅ Access monitoring
- ✅ Error tracking
- ✅ Performance monitoring

## 🔍 **Security Checklist**

### **Before Production Deployment**

- [ ] **Environment Variables**: All secrets externalized
- [ ] **HTTPS**: SSL/TLS certificates configured
- [ ] **CORS**: Restricted to specific domains
- [ ] **Headers**: Security headers configured
- [ ] **Logging**: Sensitive data not logged
- [ ] **Database**: Secure connection and credentials
- [ ] **Monitoring**: Health checks and alerts
- [ ] **Backup**: Database backup strategy
- [ ] **Updates**: Regular security updates plan

### **Ongoing Security**

- [ ] **Vulnerability Scanning**: Regular dependency updates
- [ ] **Access Review**: Regular access audit
- [ ] **Incident Response**: Security incident plan
- [ ] **Training**: Security awareness for team

## 🚨 **Security Considerations for Open Source**

### **1. Transparency**
- ✅ Security through openness
- ✅ Public security advisories
- ✅ Clear vulnerability reporting
- ✅ Community security reviews

### **2. Flexibility**
- ✅ Multiple deployment options
- ✅ Configurable security levels
- ✅ Easy customization
- ✅ Clear documentation

### **3. Community**
- ✅ Security contribution guidelines
- ✅ Bug bounty programs (if applicable)
- ✅ Security-focused discussions
- ✅ Regular security reviews

## 📞 **Getting Help**

### **Security Issues**
- Create a GitHub issue with `[SECURITY]` label
- Email security team (if applicable)
- Follow responsible disclosure guidelines

### **Security Questions**
- Check this documentation
- Review GitHub discussions
- Ask in community forums

## 🔗 **Additional Resources**

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Security Headers](https://securityheaders.com/)
- [Mozilla Security Guidelines](https://infosec.mozilla.org/guidelines/)

---

**Remember**: Security is a journey, not a destination. Regular reviews and updates are essential for maintaining a secure application. 