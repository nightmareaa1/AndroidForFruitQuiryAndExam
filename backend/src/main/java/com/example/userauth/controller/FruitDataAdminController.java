package com.example.userauth.controller;

import com.example.userauth.dto.*;
import com.example.userauth.entity.Fruit;
import com.example.userauth.entity.FruitData;
import com.example.userauth.entity.FruitDataField;
import com.example.userauth.repository.FruitDataFieldRepository;
import com.example.userauth.repository.FruitDataRepository;
import com.example.userauth.repository.FruitRepository;
import com.example.userauth.security.RequireAdmin;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/fruit-data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FruitDataAdminController {

    private static final Logger logger = LoggerFactory.getLogger(FruitDataAdminController.class);

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private FruitDataRepository fruitDataRepository;

    @Autowired
    private FruitDataFieldRepository fieldRepository;

    // ==================== 数据类型管理 ====================

    /**
     * 获取所有数据类型（从字段定义中提取唯一的fieldType）
     */
    @GetMapping("/data-types")
    public ResponseEntity<List<DataTypeInfo>> getAllDataTypes() {
        List<FruitDataField> allFields = fieldRepository.findAll();
        Map<String, List<FruitDataField>> fieldsByType = allFields.stream()
                .collect(Collectors.groupingBy(FruitDataField::getFieldType));

        List<DataTypeInfo> dataTypes = fieldsByType.entrySet().stream()
                .map(entry -> new DataTypeInfo(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .filter(FruitDataField::getIsActive)
                                .count()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dataTypes);
    }

    /**
     * 添加新数据类型（通过添加第一个字段来创建）
     */
    @PostMapping("/data-types")
    public ResponseEntity<?> addDataType(@Valid @RequestBody DataTypeCreateRequest request) {
        String dataType = request.getDataType();

        // 检查数据类型是否已存在
        List<FruitDataField> existingFields = fieldRepository
                .findByFieldTypeAndIsActiveTrueOrderByDisplayOrder(dataType);
        if (!existingFields.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(createError("数据类型 '" + dataType + "' 已存在"));
        }

        // 创建数据类型的第一个字段
        FruitDataField field = new FruitDataField();
        field.setFieldType(dataType);
        field.setFieldName(request.getFirstFieldName());
        field.setFieldUnit(request.getFirstFieldUnit());
        field.setDisplayOrder(0);
        field.setIsActive(true);

        FruitDataField saved = fieldRepository.save(field);

        Map<String, Object> result = new HashMap<>();
        result.put("dataType", dataType);
        result.put("field", saved);
        result.put("message", "数据类型创建成功");

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 删除数据类型（删除该类型下的所有字段和数据）
     */
    @DeleteMapping("/data-types/{dataType}")
    @Transactional
    public ResponseEntity<?> deleteDataType(@PathVariable String dataType) {
        logger.info("Deleting data type: {}", dataType);

        try {
            // 1. 删除该数据类型下的所有数据
            List<FruitData> dataList = fruitDataRepository.findByDataType(dataType);
            if (!dataList.isEmpty()) {
                fruitDataRepository.deleteAll(dataList);
                logger.info("Deleted {} data records for type: {}", dataList.size(), dataType);
            }

            // 2. 删除该数据类型的所有字段定义
            List<FruitDataField> fields = fieldRepository.findAll().stream()
                    .filter(f -> f.getFieldType().equals(dataType))
                    .collect(Collectors.toList());
            if (!fields.isEmpty()) {
                fieldRepository.deleteAll(fields);
                logger.info("Deleted {} field definitions for type: {}", fields.size(), dataType);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "数据类型 '" + dataType + "' 已删除");
            result.put("deletedDataCount", dataList.size());
            result.put("deletedFieldCount", fields.size());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to delete data type: {}", dataType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createError("删除失败: " + e.getMessage()));
        }
    }

    // ==================== 表格数据管理（单条CRUD）====================

    /**
     * 获取指定表格的所有数据
     */
    @GetMapping("/data")
    public ResponseEntity<?> getTableData(
            @RequestParam String fruitName,
            @RequestParam String dataType) {
        Optional<FruitData> dataOpt = fruitDataRepository
                .findByFruitNameAndDataType(fruitName, dataType);

        if (dataOpt.isPresent()) {
            return ResponseEntity.ok(dataOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 添加或更新单条数据
     */
    @PostMapping("/data")
    public ResponseEntity<?> addOrUpdateData(@Valid @RequestBody FruitDataRequest request) {
        try {
            Optional<Fruit> fruitOpt = fruitRepository.findByName(request.getFruitName());
            if (fruitOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createError("水果不存在: " + request.getFruitName()));
            }

            String fruitName = fruitOpt.get().getName();
            String dataType = request.getDataType();

            // 查找现有数据或创建新数据
            Optional<FruitData> dataOpt = fruitDataRepository
                    .findByFruitNameAndDataType(fruitName, dataType);

            FruitData fruitData;
            if (dataOpt.isPresent()) {
                fruitData = dataOpt.get();
                Map<String, Double> existingValues = fruitData.getDataValues();
                if (existingValues == null) {
                    existingValues = new HashMap<>();
                }
                existingValues.put(request.getFieldName(), request.getValue());
                fruitData.setDataValues(existingValues);
            } else {
                fruitData = new FruitData();
                fruitData.setFruitName(fruitName);
                fruitData.setDataType(dataType);
                Map<String, Double> newValues = new HashMap<>();
                newValues.put(request.getFieldName(), request.getValue());
                fruitData.setDataValues(newValues);
            }

            FruitData saved = fruitDataRepository.save(fruitData);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "数据保存成功");
            result.put("data", saved);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to save data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createError("保存失败: " + e.getMessage()));
        }
    }

    /**
     * 删除单条数据（某个字段的值）
     */
    @DeleteMapping("/data")
    public ResponseEntity<?> deleteDataField(
            @RequestParam String fruitName,
            @RequestParam String dataType,
            @RequestParam String fieldName) {
        Optional<FruitData> dataOpt = fruitDataRepository
                .findByFruitNameAndDataType(fruitName, dataType);

        if (dataOpt.isPresent()) {
            FruitData fruitData = dataOpt.get();
            Map<String, Double> values = fruitData.getDataValues();

            if (values != null && values.containsKey(fieldName)) {
                values.remove(fieldName);

                if (values.isEmpty()) {
                    // 如果没有数据了，删除整条记录
                    fruitDataRepository.delete(fruitData);
                } else {
                    fruitData.setDataValues(values);
                    fruitDataRepository.save(fruitData);
                }

                return ResponseEntity.ok(createSuccess("字段 '" + fieldName + "' 已删除"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createError("字段不存在: " + fieldName));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除整个表格（某fruit_name + data_type的所有数据）
     */
    @DeleteMapping("/table")
    @Transactional
    public ResponseEntity<?> deleteTable(
            @RequestParam String fruitName,
            @RequestParam String dataType) {
        logger.info("Deleting table for fruit: {}, dataType: {}", fruitName, dataType);

        try {
            List<FruitData> dataList = fruitDataRepository.findByFruitNameAndDataType(fruitName, dataType)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());

            if (!dataList.isEmpty()) {
                fruitDataRepository.deleteAll(dataList);
                logger.info("Deleted {} records for table: {}-{}", dataList.size(), fruitName, dataType);

                return ResponseEntity.ok(createSuccess("表格 '" + fruitName + " - " + dataType + "' 已删除"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createError("表格不存在: " + fruitName + " - " + dataType));
            }
        } catch (Exception e) {
            logger.error("Failed to delete table", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createError("删除失败: " + e.getMessage()));
        }
    }

    // ==================== 原有的字段管理（保持不变）====================

    // 获取第一字段选项（水果列表）
    @GetMapping("/fruits")
    public ResponseEntity<List<FruitOption>> getFruits() {
        List<Fruit> fruits = fruitRepository.findAll();
        List<FruitOption> options = fruits.stream()
                .map(f -> new FruitOption(f.getId(), f.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(options);
    }

    // 获取第二字段选项（营养/风味指标）
    @GetMapping("/fields/{dataType}")
    public ResponseEntity<List<FieldOption>> getFields(@PathVariable String dataType) {
        List<FruitDataField> fields = fieldRepository.findByFieldTypeAndIsActiveTrueOrderByDisplayOrder(dataType);
        List<FieldOption> options = fields.stream()
                .map(f -> new FieldOption(f.getId(), f.getFieldName(), f.getFieldUnit()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(options);
    }

    // 添加第二字段选项
    @PostMapping("/fields")
    public ResponseEntity<?> addField(@Valid @RequestBody FieldRequest request) {
        if (fieldRepository.existsByFieldTypeAndFieldName(request.getFieldType(), request.getFieldName())) {
            return ResponseEntity.badRequest().body(createError("该字段已存在"));
        }

        FruitDataField field = new FruitDataField();
        field.setFieldType(request.getFieldType());
        field.setFieldName(request.getFieldName());
        field.setFieldUnit(request.getFieldUnit());
        field.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        FruitDataField saved = fieldRepository.save(field);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 删除第二字段选项
    @DeleteMapping("/fields/{id}")
    public ResponseEntity<Void> deleteField(@PathVariable Long id) {
        if (!fieldRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fieldRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 上传CSV文件并导入数据
    @PostMapping("/import")
    public ResponseEntity<?> importCsv(
            @RequestParam String dataType,
            @RequestParam("file") MultipartFile file) {
        logger.info("Importing CSV file for data type: {}", dataType);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(createError("文件不能为空"));
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest().body(createError("只能上传CSV文件"));
        }

        try {
            List<Map<String, String>> csvData = parseCsv(file);
            int imported = 0;

            for (Map<String, String> row : csvData) {
                String fruitName = row.get("fruit");
                String fieldName = row.get("field");
                String valueStr = row.get("value");

                if (fruitName == null || fieldName == null || valueStr == null) {
                    continue;
                }

                Optional<Fruit> fruitOpt = fruitRepository.findByName(fruitName);
                if (fruitOpt.isEmpty()) {
                    logger.warn("Fruit not found: {}", fruitName);
                    continue;
                }

                double value = Double.parseDouble(valueStr);
                Fruit fruit = fruitOpt.get();

                Optional<FruitData> dataOpt = fruitDataRepository.findByFruitNameAndDataType(fruit.getName(), dataType);
                FruitData fruitData;

                if (dataOpt.isPresent()) {
                    fruitData = dataOpt.get();
                    Map<String, Double> existingValues = fruitData.getDataValues();
                    if (existingValues == null) {
                        existingValues = new HashMap<>();
                    }
                    existingValues.put(fieldName, value);
                    fruitData.setDataValues(existingValues);
                } else {
                    fruitData = new FruitData();
                    fruitData.setFruitName(fruit.getName());
                    fruitData.setDataType(dataType);
                    Map<String, Double> newValues = new HashMap<>();
                    newValues.put(fieldName, value);
                    fruitData.setDataValues(newValues);
                }

                fruitDataRepository.save(fruitData);
                imported++;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("imported", imported);
            result.put("total", csvData.size());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to import CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createError("文件解析失败: " + e.getMessage()));
        }
    }

    // 批量导入（支持多行多列）
    @PostMapping("/import/batch")
    public ResponseEntity<?> importBatch(
            @RequestParam String dataType,
            @RequestParam("file") MultipartFile file) {
        logger.info("Batch importing CSV file for data type: {}", dataType);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(createError("文件不能为空"));
        }

        try {
            List<Map<String, String>> csvData = parseCsv(file);
            Map<String, Map<String, Double>> fruitFieldValues = new HashMap<>();

            for (Map<String, String> row : csvData) {
                String fruitName = row.get("fruit");
                String fieldName = row.get("field");
                String valueStr = row.get("value");

                if (fruitName == null || fieldName == null || valueStr == null) {
                    continue;
                }

                double value = Double.parseDouble(valueStr);
                fruitFieldValues.computeIfAbsent(fruitName, k -> new HashMap<>())
                        .put(fieldName, value);
            }

            int imported = 0;
            for (Map.Entry<String, Map<String, Double>> entry : fruitFieldValues.entrySet()) {
                String fruitName = entry.getKey();
                Map<String, Double> values = entry.getValue();

                Optional<Fruit> fruitOpt = fruitRepository.findByName(fruitName);
                if (fruitOpt.isEmpty()) {
                    continue;
                }

                Fruit fruit = fruitOpt.get();
                Optional<FruitData> dataOpt = fruitDataRepository.findByFruitNameAndDataType(fruit.getName(), dataType);
                FruitData fruitData;

                if (dataOpt.isPresent()) {
                    fruitData = dataOpt.get();
                    Map<String, Double> existingValues = fruitData.getDataValues();
                    if (existingValues == null) {
                        existingValues = new HashMap<>();
                    }
                    existingValues.putAll(values);
                    fruitData.setDataValues(existingValues);
                } else {
                    fruitData = new FruitData();
                    fruitData.setFruitName(fruit.getName());
                    fruitData.setDataType(dataType);
                    fruitData.setDataValues(values);
                }

                fruitDataRepository.save(fruitData);
                imported++;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("imported", imported);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to batch import CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createError("文件解析失败: " + e.getMessage()));
        }
    }

    private List<Map<String, String>> parseCsv(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new Exception("CSV文件为空");
            }

            String[] headers = headerLine.split(",");
            if (headers.length < 3) {
                throw new Exception("CSV格式错误，需要至少3列：fruit,field,value");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    Map<String, String> row = new HashMap<>();
                    row.put("fruit", values[0].trim());
                    row.put("field", values[1].trim());
                    row.put("value", values[2].trim());
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    private ErrorResponse createError(String message) {
        return new ErrorResponse(message);
    }

    // DTO Classes
    public static class FruitOption {
        private Long id;
        private String name;

        public FruitOption(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }

    public static class FieldOption {
        private Long id;
        private String name;
        private String unit;

        public FieldOption(Long id, String name, String unit) {
            this.id = id;
            this.name = name;
            this.unit = unit;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getUnit() { return unit; }
    }

    public static class FieldRequest {
        private String fieldType;
        private String fieldName;
        private String fieldUnit;
        private Integer displayOrder;

        public String getFieldType() { return fieldType; }
        public void setFieldType(String fieldType) { this.fieldType = fieldType; }
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public String getFieldUnit() { return fieldUnit; }
        public void setFieldUnit(String fieldUnit) { this.fieldUnit = fieldUnit; }
        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    }

    public static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }

    public static class DataTypeInfo {
        private String dataType;
        private long totalFields;
        private long activeFields;

        public DataTypeInfo(String dataType, long totalFields, long activeFields) {
            this.dataType = dataType;
            this.totalFields = totalFields;
            this.activeFields = activeFields;
        }

        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        public long getTotalFields() { return totalFields; }
        public void setTotalFields(long totalFields) { this.totalFields = totalFields; }
        public long getActiveFields() { return activeFields; }
        public void setActiveFields(long activeFields) { this.activeFields = activeFields; }
    }

    public static class DataTypeCreateRequest {
        private String dataType;
        private String firstFieldName;
        private String firstFieldUnit;

        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        public String getFirstFieldName() { return firstFieldName; }
        public void setFirstFieldName(String firstFieldName) { this.firstFieldName = firstFieldName; }
        public String getFirstFieldUnit() { return firstFieldUnit; }
        public void setFirstFieldUnit(String firstFieldUnit) { this.firstFieldUnit = firstFieldUnit; }
    }

    public static class FruitDataRequest {
        private String fruitName;
        private String dataType;
        private String fieldName;
        private Double value;

        public String getFruitName() { return fruitName; }
        public void setFruitName(String fruitName) { this.fruitName = fruitName; }
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }

    private Map<String, Object> createSuccess(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        return result;
    }
}
