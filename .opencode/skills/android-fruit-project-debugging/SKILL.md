---
name: android-fruit-project-debugging
description: Debug Android + Spring Boot project integration issues. Use when: (1) Android data not displaying, (2) API requests failing (400/500), (3) Gson/JSON parsing errors, (4) UTF-8 encoding issues, (5) Hilt dependency injection failures, (6) backend validation errors, (7) navigation not working, (8) file upload issues. Includes problem type quick reference, key file locations, debugging commands, and common solutions.
---

# Android Fruit Project Debugging

## Project Structure
```
android-app/     # Kotlin + Jetpack Compose + Hilt
backend/         # Spring Boot + MySQL + Redis
docs/           # Documentation
```

## Code Navigation Flow
```
UI Screen → ViewModel → Repository → API → Backend
     ↓           ↓          ↓          ↓
Composable  StateFlow   suspend    Retrofit
```

## Key Files Quick Reference

### Navigation
- `ui/navigation/Screen.kt` - Route definitions (e.g., `competition_edit/{competitionId}`)
- `ui/navigation/NavGraph.kt` - Route implementations with NavArgument

### Data Layer
- `data/repository/CompetitionRepository.kt` - Submit entries, CRUD competitions
- `data/api/CompetitionApi.kt` - Retrofit interface
- `data/api/dto/CompetitionDtos.kt` - DTO definitions

### ViewModels
- `viewmodel/EntryAddViewModel.kt` - Entry submission state
- `viewmodel/CompetitionManagementViewModel.kt` - Competition CRUD

## Problem Type Quick Reference

| Symptom | Likely Cause | Check Here |
|---------|-------------|------------|
| 400/500 errors | Validation rules | `backend/.../service/` |
| Android data empty | Gson parsing | `android-app/.../di/NetworkModule.kt` |
| UTF-8 garbled | Encoding config | `application.yml` + `NetworkModule.kt` |
| ViewModel crash | Missing @HiltViewModel | `android-app/.../viewmodel/` |
| API path wrong | Duplicate "api/" prefix | `android-app/.../api/*.kt` |
| Edit button no response | Nav argument type mismatch | `NavGraph.kt` |
| filePath null | ContentResolver file reading | `CompetitionRepository.kt` |

## Common Issues & Fixes

### 1. Navigation: Long Argument Type Mismatch
**Symptom**: Edit button click does nothing, screen doesn't open

**Cause**: Route uses `NavType.LongType` but retrieved via `getString().toLongOrNull()`

```kotlin
// WRONG - causes navigation to fail
val competitionId = backStack.arguments?.getString("competitionId")?.toLongOrNull()

// CORRECT - direct Long retrieval
val competitionId = backStack.arguments?.getLong("competitionId")
```

**File**: `NavGraph.kt` composable for CompetitionEdit

### 2. Image Upload: filePath Always Null
**Symptom**: Backend receives entry but filePath is null, images don't display

**Root Cause**: Android sent `content://` URI but code tried to use `java.io.File(uri)` which doesn't work

**Debug Steps**:
1. Check Android logcat for file upload HTTP request
2. Check backend logs for `Processing file:` message
3. Verify file bytes are sent

**Fix**: Use ContentResolver to read file bytes
```kotlin
// In CompetitionRepository.kt - inject @ApplicationContext Context
val inputStream = context.contentResolver.openInputStream(uri)
val fileBytes = inputStream?.use { it.readBytes() }
val filePart = if (fileBytes != null) {
    MultipartBody.Part.createFormData("file", fileName, requestBody)
} else null
```

### 3. Edit Screen Shows Blank/Closes Immediately
**Symptom**: CompetitionEditScreen loads but closes or shows loading forever

**Cause**: Data not loaded before checking if competition exists

**Fix**: Use remember() and show loading state
```kotlin
val competition = remember(competitions, competitionId) {
    competitions.find { it.id == competitionId }
}

if (competition == null && isLoading) {
    CircularProgressIndicator()
    return@CompetitionEditScreen
}
```

### 4. Backend-Android DTO Field Mismatch
**Symptom**: JSON parsing works but field is null

**Check**: Backend entity field vs Android DTO field
```java
// Backend: CompetitionEntry.java
private String filePath;

// Android: CompetitionEntryDtos.kt - MUST match!
data class CompetitionEntryDto(
    val filePath: String? = null
)
```

### 5. Multipart File Upload - Wrong Part Name
**Symptom**: Backend receives empty file

**Check**: Retrofit @Part name matches backend @RequestPart
```kotlin
// Android - must be "file"
MultipartBody.Part.createFormData("file", fileName, requestBody)

// Backend - must match!
@RequestPart("file") MultipartFile file
```

## Backend API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/competitions/{id}/submit` | Submit entry with optional file |
| GET | `/api/ratings/{competitionId}` | Get rating data with filePath |
| PUT | `/api/competitions/{id}` | Update competition |
| GET | `/api/files/{filename}` | Serve uploaded files |

## File Upload Flow
```
Android: ContentResolver → ByteArray → MultipartBody.Part → Retrofit
                                    ↓
Backend: MultipartFile → FileStorageService.storeFile() → filename
                                    ↓
Database: competition_entries.file_path = filename
                                    ↓
Display: URL + filename → AsyncImage (Coil)
```

## Debugging Commands

```bash
# Android compilation
cd android-app && ./gradlew :app:compileDebugKotlin

# Backend compilation
cd backend && mvn compile

# Health check
curl http://localhost:8080/actuator/health

# Get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"android_admin","password":"Android123!"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# Backend logs (Docker)
docker logs userauth-backend-dev --tail 100 -f

# Database check
docker exec userauth-mysql-dev mysql -u userauth -p'SecureDbPass456$%^' -D userauth_dev \
  -e "SELECT id, name, file_path FROM competition_entries LIMIT 5;"
```

## Quick Search Patterns

```bash
# Find all navigation routes
grep -r "Screen\." --include="*.kt" android-app/ui/navigation/

# Find API definitions
grep -r "suspend fun" --include="*.kt" android-app/data/api/

# Find backend endpoints
grep -r "@PostMapping\|@GetMapping" --include="*.java" backend/

# Find entity definitions
grep -r "class CompetitionEntry" --include="*.java" backend/

# Find file upload handling
grep -r "submitEntry\|MultipartBody" --include="*.kt" android-app/
```

## Reading Order

1. `docs/README.md` - Project overview
2. `docs/development/android-backend-crud-integration-plan.md` - Integration plan
3. For navigation issues → Check `Screen.kt` → `NavGraph.kt`
4. For data issues → Check Repository → API → DTOs → Entity
5. For upload issues → Check ContentResolver → Multipart → Backend FileStorageService
