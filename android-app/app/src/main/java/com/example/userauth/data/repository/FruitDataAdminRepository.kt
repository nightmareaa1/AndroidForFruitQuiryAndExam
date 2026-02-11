package com.example.userauth.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.userauth.data.api.FruitDataAdminApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FruitDataAdminRepository @Inject constructor(
    private val api: FruitDataAdminApi,
    @ApplicationContext private val context: Context
) {
    /**
     * 上传 CSV 文件并导入数据
     * @param fileUri 文件 URI（支持 content:// 和 file:// 格式）
     * @param dataType 数据类型
     * @return 成功返回导入的记录数量，失败返回异常信息
     */
    suspend fun uploadCsv(fileUri: Uri, dataType: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            // 使用 ContentResolver 读取文件（正确处理 content:// URI）
            val inputStream = context.contentResolver.openInputStream(fileUri)
                ?: return@withContext Result.failure(Exception("无法打开文件，请检查文件权限"))

            val fileBytes = inputStream.use { it.readBytes() }

            if (fileBytes.isEmpty()) {
                return@withContext Result.failure(Exception("文件内容为空"))
            }

            // 获取文件名
            val fileName = getFileName(fileUri) ?: "import_${System.currentTimeMillis()}.csv"

            // 获取 MIME 类型
            val mimeType = context.contentResolver.getType(fileUri) ?: "text/csv"

            // 创建 MultipartBody.Part
            val requestFile = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)

            // 创建 dataType 参数
            val dataTypePart = dataType.toRequestBody("text/plain".toMediaTypeOrNull())

            // 执行上传
            val response = api.importCsv(dataTypePart, filePart)

            if (response.isSuccessful) {
                val importedValue = response.body()?.get("imported")
                val imported = when (importedValue) {
                    is Int -> importedValue
                    is Double -> importedValue.toInt()
                    is Long -> importedValue.toInt()
                    is String -> importedValue.toIntOrNull() ?: 0
                    else -> 0
                }
                Result.success(imported)
            } else {
                val errorBody = response.body()?.toString() ?: response.message()
                Result.failure(Exception("上传失败: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("文件处理失败: ${e.message}"))
        }
    }

    /**
     * 从 URI 获取文件名
     */
    private fun getFileName(uri: Uri): String? {
        return try {
            if (uri.scheme == "content") {
                var result: String? = null
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index >= 0) {
                            result = cursor.getString(index)
                        }
                    }
                }
                if (result != null) return result
            }
            // Fallback: 从 path 中提取文件名
            val path = uri.path ?: return null
            val cut = path.lastIndexOf('/')
            if (cut != -1 && cut < path.length - 1) {
                path.substring(cut + 1)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
