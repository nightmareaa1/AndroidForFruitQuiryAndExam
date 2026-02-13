package com.example.userauth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userauth.data.api.*
import com.example.userauth.data.repository.FruitDataAdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FruitDataManagementViewModel @Inject constructor(
    private val api: FruitDataAdminApi,
    private val fruitAdminApi: com.example.userauth.data.api.FruitAdminApi,
    private val fruitDataAdminRepository: FruitDataAdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FruitDataManagementUiState())
    val uiState: StateFlow<FruitDataManagementUiState> = _uiState.asStateFlow()

    init {
        loadDataTypes()
        loadFruits()
    }

    fun loadDataTypes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            api.getAllDataTypes()
                .takeIf { it.isSuccessful }
                ?.body()
                ?.let { dataTypes ->
                    _uiState.value = _uiState.value.copy(
                        dataTypes = dataTypes.map { it.dataType },
                        isLoading = false
                    )
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }

    fun loadFruits() {
        viewModelScope.launch {
            api.getFruits()
                .takeIf { it.isSuccessful }
                ?.body()
                ?.let { fruits ->
                    _uiState.value = _uiState.value.copy(fruits = fruits)
                }
        }
    }

    fun selectDataType(dataType: String?) {
        _uiState.value = _uiState.value.copy(
            selectedDataType = dataType,
            tableData = null
        )
        dataType?.let { loadFields(it) }
    }

    fun selectFruit(fruit: FruitOption?) {
        _uiState.value = _uiState.value.copy(
            selectedFruit = fruit,
            tableData = null
        )
    }

    fun loadFields(dataType: String) {
        viewModelScope.launch {
            api.getFields(dataType)
                .takeIf { it.isSuccessful }
                ?.body()
                ?.let { fields ->
                    _uiState.value = _uiState.value.copy(fields = fields)
                }
        }
    }

    fun checkTableExists() {
        val fruitName = _uiState.value.selectedFruit?.name ?: return
        val dataType = _uiState.value.selectedDataType ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val response = api.getTableData(fruitName, dataType)
            if (response.isSuccessful) {
                _uiState.value = _uiState.value.copy(
                    tableData = response.body(),
                    tableExists = true,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    tableExists = false,
                    tableData = null,
                    isLoading = false
                )
            }
        }
    }

    fun createDataType(dataType: String, firstFieldName: String, firstFieldUnit: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val request = DataTypeCreateRequest(dataType, firstFieldName, firstFieldUnit)
            api.createDataType(request)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(
                        success = "数据类型创建成功",
                        isLoading = false
                    )
                    loadDataTypes()
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(
                        error = "创建失败",
                        isLoading = false
                    )
                }
        }
    }

    fun deleteDataType(dataType: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            api.deleteDataType(dataType)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(
                        success = "数据类型已删除",
                        isLoading = false
                    )
                    loadDataTypes()
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(
                        error = "删除失败",
                        isLoading = false
                    )
                }
        }
    }

    fun addField(fieldName: String, fieldUnit: String?) {
        val dataType = _uiState.value.selectedDataType ?: return
        viewModelScope.launch {
            val request = FieldCreateRequest(dataType, fieldName, fieldUnit)
            api.addField(request)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(success = "字段添加成功")
                    loadFields(dataType)
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(error = "添加失败")
                }
        }
    }

    fun deleteField(fieldId: Long) {
        viewModelScope.launch {
            api.deleteField(fieldId)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(success = "字段已删除")
                    _uiState.value.selectedDataType?.let { loadFields(it) }
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(error = "删除失败")
                }
        }
    }

    fun addOrUpdateData(fieldName: String, value: Double) {
        val fruitName = _uiState.value.selectedFruit?.name ?: return
        val dataType = _uiState.value.selectedDataType ?: return

        viewModelScope.launch {
            val request = FruitDataFieldRequest(fruitName, dataType, fieldName, value)
            api.addOrUpdateData(request)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(success = "数据保存成功")
                    checkTableExists()
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(error = "保存失败")
                }
        }
    }

    fun deleteDataField(fieldName: String) {
        val fruitName = _uiState.value.selectedFruit?.name ?: return
        val dataType = _uiState.value.selectedDataType ?: return

        viewModelScope.launch {
            api.deleteDataField(fruitName, dataType, fieldName)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(success = "数据已删除")
                    checkTableExists()
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(error = "删除失败")
                }
        }
    }

    fun deleteTable() {
        val fruitName = _uiState.value.selectedFruit?.name ?: return
        val dataType = _uiState.value.selectedDataType ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            api.deleteTable(fruitName, dataType)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(
                        success = "表格已删除",
                        tableExists = false,
                        tableData = null,
                        isLoading = false
                    )
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(
                        error = "删除失败",
                        isLoading = false
                    )
                }
        }
    }

    fun uploadFile(fileUri: Uri, dataType: String) {
        val fruitName = _uiState.value.selectedFruit?.name ?: run {
            _uiState.value = _uiState.value.copy(error = "请先选择水果", isLoading = false)
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            fruitDataAdminRepository.uploadCsv(fileUri, dataType, fruitName)
                .onSuccess { imported ->
                    _uiState.value = _uiState.value.copy(
                        success = "导入成功，共 $imported 条数据",
                        isLoading = false
                    )
                    checkTableExists()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "导入失败",
                        isLoading = false
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = null)
    }

    fun createFruit(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val request = com.example.userauth.data.api.FruitRequest(name)
            fruitAdminApi.createFruit(request)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(
                        success = "水果创建成功",
                        isLoading = false
                    )
                    loadFruits()
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(
                        error = "创建失败",
                        isLoading = false
                    )
                }
        }
    }

    fun deleteFruit(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            fruitAdminApi.deleteFruit(id)
                .takeIf { it.isSuccessful }
                ?.let {
                    _uiState.value = _uiState.value.copy(
                        success = "水果已删除",
                        isLoading = false
                    )
                    loadFruits()
                }
                ?: run {
                    _uiState.value = _uiState.value.copy(
                        error = "删除失败",
                        isLoading = false
                    )
                }
        }
    }
}

data class FruitDataManagementUiState(
    val dataTypes: List<String> = emptyList(),
    val fruits: List<FruitOption> = emptyList(),
    val fields: List<FieldOption> = emptyList(),
    val selectedDataType: String? = null,
    val selectedFruit: FruitOption? = null,
    val tableExists: Boolean = false,
    val tableData: FruitDataEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)
