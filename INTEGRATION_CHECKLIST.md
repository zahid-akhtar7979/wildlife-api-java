# Frontend Integration Checklist

## Current Status: ❌ NOT INTEGRATED

The wildlife-ui frontend is currently configured for the Node.js backend (port 3001), not the Java backend (port 8080).

## Required Changes for Java Backend Integration

### 1. ❌ Create Authentication Controller
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/login")
    @PostMapping("/register") 
    @GetMapping("/me")
    @PutMapping("/update-profile")
    @PutMapping("/change-password")
}
```

### 2. ❌ JWT Authentication Service
- User login endpoint
- User registration endpoint
- Token generation and validation
- Password hashing (BCrypt)

### 3. ✅ Article API (Already exists)
- `/api/articles` endpoints are implemented
- Compatible with frontend expectations

### 4. ❌ File Upload API Compatibility
- Check if current upload endpoints match frontend expectations
- Frontend expects: `/api/upload/image`, `/api/upload/video`
- Java has: Upload controller but needs verification

### 5. ❌ Port Configuration
- **Option A:** Change Java backend to port 3001
- **Option B:** Update frontend to use port 8080

### 6. ❌ Response Format Compatibility
Frontend expects:
```json
{
  "success": true,
  "message": "...",
  "data": {...},
  "token": "..." // for auth endpoints
}
```

## Current Working Setup (Node.js)

The frontend is currently integrated with:
- **Backend:** wildlife-api (Node.js)
- **Port:** 3001
- **Database:** PostgreSQL with Prisma
- **Authentication:** JWT with bcrypt
- **Status:** ✅ FULLY FUNCTIONAL

## Recommendation

**Option 1: Continue with Node.js backend** (Working now)
- All endpoints implemented
- Authentication working
- File uploads working
- Frontend already integrated

**Option 2: Migrate to Java backend** (Requires work)
1. Implement missing authentication endpoints
2. Verify upload API compatibility  
3. Ensure response format matches
4. Update port configuration
5. Test all frontend integrations

## Migration Steps (If choosing Java backend)

1. **Create AuthController.java**
2. **Create AuthService.java** 
3. **Add JWT utility classes**
4. **Update application.yml** (port to 3001)
5. **Test authentication flow**
6. **Verify upload endpoints**
7. **Test frontend integration**
8. **Update frontend if needed**

## Files to Create/Modify

### New Files Needed:
- `src/main/java/com/wildlife/auth/api/AuthController.java`
- `src/main/java/com/wildlife/auth/service/AuthService.java`
- `src/main/java/com/wildlife/auth/dto/LoginRequest.java`
- `src/main/java/com/wildlife/auth/dto/LoginResponse.java`
- `src/main/java/com/wildlife/auth/dto/RegisterRequest.java`

### Files to Modify:
- `application.yml` (change server port to 3001)
- `SecurityConfig.java` (add auth endpoints)
- Upload controller (verify compatibility)

## Current Frontend API Calls

The frontend makes these API calls that must be supported:

### Authentication:
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/me`
- `PUT /api/auth/update-profile`
- `PUT /api/auth/change-password`

### Articles:
- `GET /api/articles`
- `GET /api/articles/featured`
- `GET /api/articles/{id}`
- `POST /api/articles`
- `PUT /api/articles/{id}`
- `DELETE /api/articles/{id}`

### Upload:
- `POST /api/upload/image`
- `POST /api/upload/video`
- `DELETE /api/upload/delete/{publicId}`

### Users:
- `GET /api/users` (admin)
- `POST /api/users/{id}/approve` (admin) 