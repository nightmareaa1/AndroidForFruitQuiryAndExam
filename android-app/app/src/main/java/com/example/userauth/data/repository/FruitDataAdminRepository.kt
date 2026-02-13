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
     * @param fileUri 文件 URI
     * @param dataType 数据类型（从界面选择）
     * @param fruitName 水果名称（从界面选择）
     * @return 成功返回导入的记录数量
     */
    suspend fun uploadCsv(fileUri: Uri, dataType: String, fruitName: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
                ?: return@withContext Result.failure(Exception("无法打开文件"))

            val fileBytes = inputStream.use { it.readBytes() }
            if (fileBytes.isEmpty()) {
                return@withContext Result.failure(Exception("文件内容为空"))
            }

            val fileName = getFileName(fileUri) ?: "import.csv"
            val mimeType = context.contentResolver.getType(fileUri) ?: "text/csv"

            val requestFile = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)

            val dataTypePart = dataType.toRequestBody("text/plain".toMediaTypeOrNull())
            val fruitNamePart = fruitName.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.importCsv(dataTypePart, fruitNamePart, filePart)

            if (response.isSuccessful) {
                val imported = response.body()?.get("imported")?.toString()?.toIntOrNull() ?: 0
                Result.success(imported)
            } else {
                Result.failure(Exception("上传失败: ${response.code()}"))
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
