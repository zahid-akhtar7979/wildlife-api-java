# Configuration Strategy - Enterprise Approach

This project follows **enterprise-grade configuration management** practices used by companies like Amazon, Google, Netflix, and other large-scale organizations.

## 🏗️ Configuration Architecture

### **Multiple Environment Files** (Current Approach)
```
src/main/resources/
├── application.yml          # Base configuration (common settings)
├── application-dev.yml      # Development environment
├── application-test.yml     # Testing environment  
├── application-staging.yml  # Staging environment
├── application-prod.yml     # Production environment
└── application-docker.yml   # Docker containerized environments
```

### **Why NOT Single File?**

❌ **Single File Problems:**
```yaml
# BAD: All environments in one file
spring:
  profiles:
    active: ${ENV}
---
spring:
  config:
    activate:
      on-profile: dev
# Dev secrets visible to everyone
database:
  password: dev-password
---  
spring:
  config:
    activate:
      on-profile: prod
# Production secrets in same file!
database:
  password: super-secret-prod-password
```

**Issues:**
- 🔒 **Security Risk**: Production secrets visible in development
- 🔄 **Merge Conflicts**: Multiple teams editing same file
- 📦 **Deployment Risk**: All configs deployed together
- 🚫 **Access Control**: Can't restrict who sees what environment
- 📈 **Scale Problems**: File becomes massive with many environments

## 🎯 Enterprise Best Practices

### **1. Separate Files Per Environment**

✅ **Our Current Approach:**
```yaml
# application.yml (base)
spring:
  datasource:
    url: ${DATABASE_URL}          # Environment variable
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}

# application-prod.yml (production only)  
spring:
  config:
    import:
      - aws-parameterstore:/wildlife-api/
      - aws-secretsmanager:/wildlife-api/secrets/
```

### **2. External Configuration Management**

**Large Companies Use:**

**Amazon/AWS:**
```yaml
# application-prod.yml
spring:
  config:
    import:
      - aws-parameterstore:/wildlife-api/prod/
      - aws-secretsmanager:/wildlife-api/secrets/
management:
  endpoint:
    refresh:
      enabled: true  # Dynamic config updates
```

**Google Cloud:**
```yaml
spring:
  config:
    import:
      - gcp-secretmanager:/projects/wildlife-prod/secrets/
```

**Netflix:**
```yaml
# Uses Archaius for dynamic configuration
archaius:
  configurationSource:
    defaultFileName: wildlife-config
```

### **3. Configuration Hierarchy**

```
1. External Config Systems (highest priority)
   ↓ AWS Parameter Store, Secrets Manager
2. Environment Variables  
   ↓ DATABASE_URL, JWT_SECRET
3. Profile-specific Files
   ↓ application-prod.yml
4. Base Configuration (lowest priority)
   ↓ application.yml
```

## 🔒 Security Strategy

### **Development Environment**
```yaml
# application-dev.yml - Safe to commit
keycloak:
  auth-server-url: http://localhost:8080
  credentials:
    secret: dev-client-secret  # Safe development secret
```

### **Production Environment**
```yaml
# application-prod.yml - Minimal, references external
spring:
  config:
    import:
      - aws-parameterstore:/wildlife-api/prod/
# No secrets in this file!
```

**External Parameter Store:**
```
/wildlife-api/prod/keycloak/auth-server-url = https://auth.wildlife.com
/wildlife-api/prod/keycloak/credentials/secret = [encrypted-secret]
/wildlife-api/prod/database/url = [rds-endpoint]
```

## 🚀 Deployment Strategy

### **Environment Activation**

**Development:**
```bash
java -jar wildlife-api.jar --spring.profiles.active=dev
```

**Production:**
```bash
# Environment variables override file values
export DATABASE_URL="jdbc:postgresql://prod-db:5432/wildlife"
export KEYCLOAK_ISSUER_URI="https://auth.wildlife.com/realms/wildlife"
java -jar wildlife-api.jar --spring.profiles.active=prod
```

**Docker:**
```yaml
# docker-compose.yml
environment:
  SPRING_PROFILES_ACTIVE: prod
  DATABASE_URL: jdbc:postgresql://postgres:5432/wildlife
```

### **CI/CD Pipeline Configuration**

```yaml
# .github/workflows/deploy.yml
- name: Deploy to Staging
  env:
    SPRING_PROFILES_ACTIVE: staging
    DATABASE_URL: ${{ secrets.STAGING_DB_URL }}

- name: Deploy to Production  
  env:
    SPRING_PROFILES_ACTIVE: prod
    # Production secrets come from AWS Parameter Store
```

## 📊 Configuration Management Comparison

| Approach | Security | Scalability | Maintainability | Used By |
|----------|----------|-------------|----------------|---------|
| Single File | ❌ Poor | ❌ Poor | ❌ Poor | Small Projects |
| **Multiple Files** | ✅ Good | ✅ Good | ✅ Good | **Our Approach** |
| External Config | ✅ Excellent | ✅ Excellent | ✅ Excellent | **Amazon, Google** |
| Hybrid Approach | ✅ Excellent | ✅ Excellent | ✅ Excellent | **Netflix, Uber** |

## 🔄 Migration Path

### **Phase 1: Multiple Files** ✅ (Current)
- Separate environment-specific files
- Environment variables for secrets
- Profile-based activation

### **Phase 2: External Config** (Future)
```yaml
# Add to application-prod.yml
spring:
  config:
    import:
      - aws-parameterstore:/wildlife-api/
      - aws-secretsmanager:/wildlife-api/secrets/
```

### **Phase 3: Dynamic Configuration** (Advanced)
```yaml
management:
  endpoint:
    refresh:
      enabled: true
```

## 📝 Configuration File Guidelines

### **Base Configuration (application.yml)**
- Common settings across all environments
- Default values with environment variable overrides
- No environment-specific secrets

### **Environment Files**
- **Dev**: Development-friendly settings, debug logging
- **Test**: Fast startup, in-memory databases
- **Staging**: Production-like but with more monitoring
- **Prod**: Optimized for performance and security

### **Security Rules**
1. ❌ Never commit production secrets to version control
2. ✅ Use environment variables for sensitive values
3. ✅ External configuration systems for production
4. ✅ Principle of least privilege for config access
5. ✅ Audit trail for configuration changes

## 🏢 Real-World Examples

### **Netflix Approach**
```java
@Value("${wildlife.api.timeout:5000}")
private int apiTimeout;

// Dynamic configuration with Archaius
DynamicIntProperty timeout = DynamicPropertyFactory
    .getInstance()
    .getIntProperty("wildlife.api.timeout", 5000);
```

### **Amazon Approach**
```yaml
# Minimal application.yml
spring:
  config:
    import: 
      - aws-parameterstore:/wildlife-api/${ENVIRONMENT}/
      - aws-secretsmanager:/wildlife-api/secrets/
```

### **Google Approach**  
```yaml
spring:
  config:
    import:
      - gcp-secretmanager:/projects/${PROJECT_ID}/secrets/
```

This enterprise approach ensures **security**, **scalability**, and **maintainability** at scale! 🚀 