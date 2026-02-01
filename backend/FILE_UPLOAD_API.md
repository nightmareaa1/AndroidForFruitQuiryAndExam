# File Upload API Documentation

This document describes the file upload functionality implemented in the user authentication system.

## Overview

The file upload system provides secure file storage with validation for image files. It supports uploading, downloading, and managing files through REST API endpoints.

## Configuration

File upload settings are configured in `application.yml`:

```yaml
app:
  file:
    upload-dir: ${UPLOAD_PATH:./uploads}
    max-size: ${MAX_FILE_SIZE:10MB}
    allowed-types: jpg,jpeg,png,webp
```

## API Endpoints

### Upload File
- **POST** `/api/files/upload`
- **Content-Type**: `multipart/form-data`
- **Parameter**: `file` (multipart file)

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@image.jpg"
```

**Success Response (200 OK):**
```json
{
  "filename": "uuid-generated-filename.jpg",
  "message": "File uploaded successfully"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "File type 'txt' not allowed. Allowed types: jpg, jpeg, png, webp"
}
```

### Download File
- **GET** `/api/files/{filename}`

**Example Request:**
```bash
curl http://localhost:8080/api/files/uuid-generated-filename.jpg
```

**Success Response (200 OK):**
- Returns the file with appropriate content type
- Content-Disposition: inline

**Error Response (404 Not Found):**
- File does not exist

### Delete File
- **DELETE** `/api/files/{filename}`

**Example Request:**
```bash
curl -X DELETE http://localhost:8080/api/files/uuid-generated-filename.jpg
```

**Success Response (200 OK):**
```json
{
  "message": "File deleted successfully"
}
```

**Error Response (404 Not Found):**
- File does not exist

### Get File Information
- **GET** `/api/files/{filename}/info`

**Example Request:**
```bash
curl http://localhost:8080/api/files/uuid-generated-filename.jpg/info
```

**Success Response (200 OK):**
```json
{
  "filename": "uuid-generated-filename.jpg",
  "exists": true,
  "contentType": "image/jpeg"
}
```

## File Validation

### Supported File Types
- JPG/JPEG
- PNG
- WEBP

### File Size Limit
- Maximum: 10MB (configurable)

### Security Features
- Unique filename generation using UUID
- File type validation based on extension
- File size validation
- Path traversal protection
- Dangerous filename character filtering

## Implementation Details

### Services

#### FileValidationService
- Validates file type, size, and name
- Configurable through application properties
- Prevents security vulnerabilities

#### FileStorageService
- Handles file storage operations
- Generates unique filenames
- Manages file system operations
- Uses FileValidationService for validation

#### FileController
- REST API endpoints
- Error handling
- Content type detection
- HTTP response management

### Error Handling

The system provides comprehensive error handling:

- **400 Bad Request**: Invalid file type, size, or format
- **404 Not Found**: File does not exist
- **500 Internal Server Error**: Storage or system errors

### Testing

The implementation includes comprehensive tests:

- **Unit Tests**: FileValidationService, FileStorageService
- **Integration Tests**: Complete file upload flow
- **Error Handling Tests**: Invalid file scenarios

Run tests with:
```bash
mvn test -Dtest=File*Test
```

## Usage Examples

### Java/Spring Boot Client
```java
@Autowired
private FileStorageService fileStorageService;

public String uploadFile(MultipartFile file) throws IOException {
    return fileStorageService.storeFile(file);
}
```

### JavaScript/Fetch API
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch('/api/files/upload', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => console.log('File uploaded:', data.filename));
```

### Android/Kotlin
```kotlin
val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

val call = apiService.uploadFile(body)
call.enqueue(object : Callback<UploadResponse> {
    override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
        if (response.isSuccessful) {
            val filename = response.body()?.filename
            // Handle success
        }
    }
    
    override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
        // Handle error
    }
})
```

## Security Considerations

1. **File Type Validation**: Only allowed image types are accepted
2. **File Size Limits**: Prevents large file uploads
3. **Unique Filenames**: Prevents filename conflicts and guessing
4. **Path Traversal Protection**: Validates filenames for security
5. **Content Type Detection**: Proper MIME type handling
6. **Error Information**: Limited error details to prevent information disclosure

## Configuration Options

### Environment Variables
- `UPLOAD_PATH`: Directory for file storage (default: ./uploads)
- `MAX_FILE_SIZE`: Maximum file size (default: 10MB)

### Application Properties
```yaml
app:
  file:
    upload-dir: /path/to/uploads
    max-size: 5MB
    allowed-types: jpg,png
```

## Monitoring and Maintenance

### File Storage Monitoring
- Monitor disk space usage
- Implement file cleanup policies
- Log file operations for audit

### Performance Considerations
- Consider file storage location (local vs cloud)
- Implement file compression if needed
- Monitor upload/download performance

## Future Enhancements

Potential improvements:
- Cloud storage integration (AWS S3, Google Cloud Storage)
- Image resizing and optimization
- File metadata storage
- Batch file operations
- File versioning
- Advanced security scanning