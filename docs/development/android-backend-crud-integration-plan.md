# Android-Backend CRUD 集成方案

## 1. 后端API现状分析

### 1.1 已实现的API端点

后端已经实现了完整的CRUD API：

#### 评价模型API (EvaluationModelController)
```
GET    /api/evaluation-models          - 获取所有模型
GET    /api/evaluation-models/{id}     - 获取单个模型
POST   /api/evaluation-models          - 创建模型 (Admin only)
PUT    /api/evaluation-models/{id}     - 更新模型 (Admin only)
DELETE /api/evaluation-models/{id}     - 删除模型 (Admin only)
```

#### 赛事API (CompetitionController)
```
GET    /api/competitions               - 获取所有赛事
GET    /api/competitions/{id}          - 获取单个赛事
POST   /api/competitions               - 创建赛事 (Admin only)
PUT    /api/competitions/{id}          - 更新赛事 (Admin/Creator)
DELETE /api/competitions/{id}          - 删除赛事 (Admin/Creator)
```

### 1.2 后端DTO结构

#### ModelRequest (创建/更新)
```json
{
  "name": "模型名称",
  "parameters": [
    {
      "name": "参数名称",
      "weight": 30
    }
  ]
}
```

#### ModelResponse (响应)
```json
{
  "id": 1,
  "name": "模型名称",
  "parameters": [...],
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### CompetitionRequest (创建/更新)
```json
{
  "name": "赛事名称",
  "description": "赛事描述",
  "modelId": 1,
  "deadline": "2024-12-31T23:59:59",
  "judgeIds": [1, 2, 3]
}
```

#### CompetitionResponse (响应)
```json
{
  "id": 1,
  "name": "赛事名称",
  "description": "赛事描述",
  "modelId": 1,
  "modelName": "默认评价模型",
  "creatorId": 1,
  "creatorUsername": "admin",
  "deadline": "2024-12-31T23:59:59",
  "status": "ACTIVE",
  "judges": [...],
  "entries": [...],
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

---

## 2. 当前Android端问题分析

### 2.1 问题根源

当前的Android实现使用**本地内存存储**（StateFlow + 内存列表），导致：
1. 数据仅存在于应用内存中
2. 应用重启后数据丢失
3. 无法与其他设备或后端同步
4. 无法实现真正的数据持久化

### 2.2 需要修改的组件

1. **API接口层** - 创建Retrofit接口调用后端API
2. **数据模型层** - 创建DTO类匹配后端结构
3. **Repository层** - 从本地存储改为API调用
4. **ViewModel层** - 添加网络请求状态管理

---

## 3. 实现方案

### 3.1 第一阶段：创建API接口

#### 创建 `EvaluationModelApi.kt`
```kotlin
package com.example.userauth.data.api

import com.example.userauth.data.api.dto.EvaluationModelDto
import com.example.userauth.data.api.dto.EvaluationModelRequest
import retrofit2.Response
import retrofit2.http.*

interface EvaluationModelApi {
    @GET("api/evaluation-models")
    suspend fun getAllModels(): Response<List<EvaluationModelDto>>
    
    @GET("api/evaluation-models/{id}")
    suspend fun getModelById(@Path("id") id: Long): Response<EvaluationModelDto>
    
    @POST("api/evaluation-models")
    suspend fun createModel(@Body request: EvaluationModelRequest): Response<EvaluationModelDto>
    
    @PUT("api/evaluation-models/{id}")
    suspend fun updateModel(
        @Path("id") id: Long,
        @Body request: EvaluationModelRequest
    ): Response<EvaluationModelDto>
    
    @DELETE("api/evaluation-models/{id}")
    suspend fun deleteModel(@Path("id") id: Long): Response<Void>
}
```

#### 创建 `CompetitionApi.kt`
```kotlin
package com.example.userauth.data.api

import com.example.userauth.data.api.dto.CompetitionDto
import com.example.userauth.data.api.dto.CompetitionRequest
import retrofit2.Response
import retrofit2.http.*

interface CompetitionApi {
    @GET("api/competitions")
    suspend fun getAllCompetitions(): Response<List<CompetitionDto>>
    
    @GET("api/competitions/{id}")
    suspend fun getCompetitionById(@Path("id") id: Long): Response<CompetitionDto>
    
    @POST("api/competitions")
    suspend fun createCompetition(@Body request: CompetitionRequest): Response<CompetitionDto>
    
    @PUT("api/competitions/{id}")
    suspend fun updateCompetition(
        @Path("id") id: Long,
        @Body request: CompetitionRequest
    ): Response<CompetitionDto>
    
    @DELETE("api/competitions/{id}")
    suspend fun deleteCompetition(@Path("id") id: Long): Response<Void>
}
```

### 3.2 第二阶段：创建DTO类

#### 创建 `EvaluationModelDto.kt`
```kotlin
package com.example.userauth.data.api.dto

import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EvaluationModelDto(
    val id: Long,
    val name: String,
    val parameters: List<EvaluationParameterDto>,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    fun toDomainModel(): EvaluationModel {
        return EvaluationModel(
            id = id.toString(),
            name = name,
            description = "", // API没有description字段，需要扩展后端
            parameters = parameters.map { it.toDomainModel() }
        )
    }
}

data class EvaluationParameterDto(
    val id: Long,
    val name: String,
    val weight: Int,
    val displayOrder: Int? = null
) {
    fun toDomainModel(): EvaluationParameter {
        return EvaluationParameter(
            id = id.toString(),
            name = name,
            weight = weight,
            maxScore = 10 // 默认值
        )
    }
}

data class EvaluationModelRequest(
    val name: String,
    val parameters: List<EvaluationParameterRequest>
)

data class EvaluationParameterRequest(
    val name: String,
    val weight: Int
)
```

#### 创建 `CompetitionDto.kt`
```kotlin
package com.example.userauth.data.api.dto

import com.example.userauth.data.model.Competition

data class CompetitionDto(
    val id: Long,
    val name: String,
    val description: String?,
    val modelId: Long,
    val modelName: String?,
    val creatorId: Long,
    val creatorUsername: String?,
    val deadline: String,
    val status: String,
    val judges: List<JudgeDto>?,
    val entries: List<EntryDto>?,
    val createdAt: String?,
    val updatedAt: String?
) {
    fun toDomainModel(): Competition {
        return Competition(
            id = id,
            name = name,
            modelId = modelId,
            creatorId = creatorId,
            deadline = deadline.substring(0, 10), // 取日期部分 YYYY-MM-DD
            status = status
        )
    }
}

data class JudgeDto(
    val id: Long,
    val userId: Long,
    val username: String,
    val createdAt: String?
)

data class EntryDto(
    val id: Long,
    val entryName: String,
    val description: String?,
    val filePath: String?,
    val displayOrder: Int?,
    val status: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class CompetitionRequest(
    val name: String,
    val description: String? = null,
    val modelId: Long,
    val deadline: String, // ISO-8601 format: 2024-12-31T23:59:59
    val judgeIds: List<Long>? = null
)
```

### 3.3 第三阶段：更新Repository层

#### 创建 `EvaluationModelRepository.kt`
```kotlin
package com.example.userauth.data.repository

import com.example.userauth.data.api.EvaluationModelApi
import com.example.userauth.data.api.dto.EvaluationModelDto
import com.example.userauth.data.api.dto.EvaluationModelRequest
import com.example.userauth.data.api.dto.EvaluationParameterRequest
import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EvaluationModelRepository @Inject constructor(
    private val api: EvaluationModelApi
) {
    suspend fun getAllModels(): Result<List<EvaluationModel>> {
        return try {
            val response = api.getAllModels()
            if (response.isSuccessful) {
                val models = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Result.success(models)
            } else {
                Result.failure(Exception("Failed to fetch models: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createModel(
        name: String,
        parameters: List<EvaluationParameter>
    ): Result<EvaluationModel> {
        return try {
            val request = EvaluationModelRequest(
                name = name,
                parameters = parameters.map {
                    EvaluationParameterRequest(name = it.name, weight = it.weight)
                }
            )
            val response = api.createModel(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomainModel())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to create model: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateModel(
        id: Long,
        name: String,
        parameters: List<EvaluationParameter>
    ): Result<EvaluationModel> {
        return try {
            val request = EvaluationModelRequest(
                name = name,
                parameters = parameters.map {
                    EvaluationParameterRequest(name = it.name, weight = it.weight)
                }
            )
            val response = api.updateModel(id, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomainModel())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to update model: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteModel(id: Long): Result<Unit> {
        return try {
            val response = api.deleteModel(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete model: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### 创建 `CompetitionRepository.kt`
```kotlin
package com.example.userauth.data.repository

import com.example.userauth.data.api.CompetitionApi
import com.example.userauth.data.api.dto.CompetitionRequest
import com.example.userauth.data.model.Competition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompetitionRepository @Inject constructor(
    private val api: CompetitionApi
) {
    suspend fun getAllCompetitions(): Result<List<Competition>> {
        return try {
            val response = api.getAllCompetitions()
            if (response.isSuccessful) {
                val competitions = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Result.success(competitions)
            } else {
                Result.failure(Exception("Failed to fetch competitions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createCompetition(
        name: String,
        deadline: String,
        modelId: Long,
        description: String = ""
    ): Result<Competition> {
        return try {
            // 将日期从 YYYY-MM-DD 转换为 ISO-8601 格式
            val isoDeadline = "${deadline}T23:59:59"
            val request = CompetitionRequest(
                name = name,
                description = description,
                modelId = modelId,
                deadline = isoDeadline,
                judgeIds = null
            )
            val response = api.createCompetition(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomainModel())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to create competition: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCompetition(
        id: Long,
        name: String,
        deadline: String,
        modelId: Long,
        description: String = ""
    ): Result<Competition> {
        return try {
            val isoDeadline = "${deadline}T23:59:59"
            val request = CompetitionRequest(
                name = name,
                description = description,
                modelId = modelId,
                deadline = isoDeadline,
                judgeIds = null
            )
            val response = api.updateCompetition(id, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomainModel())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to update competition: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCompetition(id: Long): Result<Unit> {
        return try {
            val response = api.deleteCompetition(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete competition: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 3.4 第四阶段：更新ViewModel

#### 更新 `ModelViewModel.kt`
```kotlin
package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.userauth.data.model.EvaluationModel
import com.example.userauth.data.model.EvaluationParameter
import com.example.userauth.data.repository.EvaluationModelRepository
import javax.inject.Inject

@HiltViewModel
class ModelViewModel @Inject constructor(
    private val repository: EvaluationModelRepository
) : ViewModel() {
    
    private val _models = MutableStateFlow<List<EvaluationModel>>(emptyList())
    val models: StateFlow<List<EvaluationModel>> = _models.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadModels()
    }
    
    fun loadModels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getAllModels()
                .onSuccess { models ->
                    _models.value = models
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            
            _isLoading.value = false
        }
    }
    
    fun addModel(name: String, description: String, parameters: List<EvaluationParameter>) {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.createModel(name, parameters)
                .onSuccess {
                    loadModels() // 重新加载列表
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }
    
    fun updateModel(updatedModel: EvaluationModel) {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.updateModel(
                id = updatedModel.id.toLong(),
                name = updatedModel.name,
                parameters = updatedModel.parameters
            )
                .onSuccess {
                    loadModels()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }
    
    fun deleteModel(id: String) {
        viewModelScope.launch {
            repository.deleteModel(id.toLong())
                .onSuccess {
                    loadModels()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }
}
```

#### 更新 `CompetitionManagementViewModel.kt`
```kotlin
package com.example.userauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.userauth.data.model.Competition
import com.example.userauth.data.repository.CompetitionRepository
import javax.inject.Inject

@HiltViewModel
class CompetitionManagementViewModel @Inject constructor(
    private val repository: CompetitionRepository
) : ViewModel() {
    
    private val _competitions = MutableStateFlow<List<Competition>>(emptyList())
    val competitions: StateFlow<List<Competition>> = _competitions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadCompetitions()
    }
    
    fun loadCompetitions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getAllCompetitions()
                .onSuccess { competitions ->
                    _competitions.value = competitions
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            
            _isLoading.value = false
        }
    }
    
    fun addCompetition(name: String, deadline: String, modelId: Long, description: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.createCompetition(name, deadline, modelId, description)
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }
    
    fun updateCompetition(updatedCompetition: Competition) {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.updateCompetition(
                id = updatedCompetition.id,
                name = updatedCompetition.name,
                deadline = updatedCompetition.deadline,
                modelId = updatedCompetition.modelId
            )
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }
    
    fun deleteCompetition(id: Long) {
        viewModelScope.launch {
            repository.deleteCompetition(id)
                .onSuccess {
                    loadCompetitions()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }
    
    fun getCompetitionById(id: Long): Competition? {
        return _competitions.value.find { it.id == id }
    }
}
```

### 3.5 第五阶段：Hilt模块配置

#### 更新 `NetworkModule.kt`
```kotlin
package com.example.userauth.di

import com.example.userauth.data.api.EvaluationModelApi
import com.example.userauth.data.api.CompetitionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideEvaluationModelApi(retrofit: Retrofit): EvaluationModelApi {
        return retrofit.create(EvaluationModelApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCompetitionApi(retrofit: Retrofit): CompetitionApi {
        return retrofit.create(CompetitionApi::class.java)
    }
}
```

---

## 4. 后端需要的小调整

### 4.1 添加模型描述字段

后端`EvaluationModel`实体和`ModelRequest/ModelResponse`需要添加description字段，以匹配Android端的模型结构。

### 4.2 添加CORS配置（已存在）

后端Controller已有`@CrossOrigin(origins = "*")`，无需修改。

### 4.3 数据库迁移

如果需要添加description字段，需要创建Flyway迁移脚本：

```sql
-- V{版本号}__add_description_to_evaluation_models.sql
ALTER TABLE evaluation_models ADD COLUMN description VARCHAR(500) NULL;
```

---

## 5. 测试步骤

### 5.1 后端启动
```bash
cd backend
./scripts/dev-start.sh  # 或 dev-start.bat for Windows
```

### 5.2 Android端测试
1. 启动Android模拟器
2. 确保后端服务运行在 http://10.0.2.2:8080 (Android访问本机服务)
3. 登录管理员账号
4. 测试模型CRUD操作
5. 测试赛事CRUD操作

### 5.3 验证数据持久化
1. 添加模型/赛事
2. 重启Android应用
3. 检查数据是否仍然显示（从后端加载）

---

## 6. 注意事项

### 6.1 网络错误处理
- 实现重试机制
- 添加离线缓存（可选）
- 显示友好的错误信息

### 6.2 权限处理
- Admin操作需要JWT token
- 确保AuthInterceptor正确添加token到请求头

### 6.3 日期格式转换
- Android: YYYY-MM-DD
- Backend: ISO-8601 (YYYY-MM-DDTHH:mm:ss)
- 需要在Repository层进行转换

### 6.4 ID类型转换
- Android端使用String ID（来自本地生成）
- Backend使用Long ID（数据库自增）
- 需要在DTO转换时处理

---

## 7. 实施优先级

1. **高优先级** - 基础CRUD
   - 创建API接口
   - 创建DTO类
   - 更新Repository
   - 更新ViewModel

2. **中优先级** - 错误处理
   - 网络错误提示
   - 加载状态显示
   - 重试机制

3. **低优先级** - 优化
   - 离线缓存
   - 数据同步策略
   - 性能优化

---

## 8. 总结

此方案将Android前端从本地内存存储迁移到后端API调用，实现：
- ✅ 数据持久化（MySQL数据库）
- ✅ 多设备数据同步
- ✅ 管理员权限控制
- ✅ 完整的CRUD功能

实施后，Android端的修改将实时保存到后端数据库，并在其他设备和会话中可见。
