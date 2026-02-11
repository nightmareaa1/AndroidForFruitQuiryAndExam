package com.example.userauth.controller;

import com.example.userauth.dto.FruitRequest;
import com.example.userauth.dto.FruitResponse;
import com.example.userauth.dto.NutritionDataRequest;
import com.example.userauth.dto.FlavorDataRequest;
import com.example.userauth.entity.Fruit;
import com.example.userauth.entity.NutritionData;
import com.example.userauth.entity.FlavorData;
import com.example.userauth.entity.FruitFile;
import com.example.userauth.repository.FruitRepository;
import com.example.userauth.repository.NutritionDataRepository;
import com.example.userauth.repository.FlavorDataRepository;
import com.example.userauth.repository.FruitFileRepository;
import com.example.userauth.security.RequireAdmin;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/fruits")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FruitAdminController {

    private static final Logger logger = LoggerFactory.getLogger(FruitAdminController.class);

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private NutritionDataRepository nutritionDataRepository;

    @Autowired
    private FlavorDataRepository flavorDataRepository;

    @Autowired
    private FruitFileRepository fruitFileRepository;

    @GetMapping
    public ResponseEntity<List<FruitResponse>> getAllFruits() {
        logger.info("GET /api/admin/fruits - Fetching all fruits");
        List<Fruit> fruits = fruitRepository.findAll();
        List<FruitResponse> responses = fruits.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FruitResponse> getFruitById(@PathVariable Long id) {
        logger.info("GET /api/admin/fruits/{} - Fetching fruit", id);
        Optional<Fruit> fruit = fruitRepository.findById(id);
        return fruit.map(f -> ResponseEntity.ok(convertToResponse(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createFruit(@Valid @RequestBody FruitRequest request) {
        logger.info("POST /api/admin/fruits - Creating new fruit: {}", request.getName());

        if (fruitRepository.existsByName(request.getName())) {
            return ResponseEntity.badRequest().body(createErrorResponse("水果名称已存在"));
        }

        Fruit fruit = new Fruit();
        fruit.setName(request.getName());

        if (request.getDescription() != null) {
            fruit.setDescription(request.getDescription());
        }

        Fruit saved = fruitRepository.save(fruit);
        logger.info("Successfully created fruit with id: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFruit(@PathVariable Long id, @Valid @RequestBody FruitRequest request) {
        logger.info("PUT /api/admin/fruits/{} - Updating fruit", id);

        Optional<Fruit> existing = fruitRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Fruit fruit = existing.get();

        if (request.getName() != null && !request.getName().isEmpty()) {
            Optional<Fruit> other = fruitRepository.findByName(request.getName());
            if (other.isPresent() && !other.get().getId().equals(id)) {
                return ResponseEntity.badRequest().body(createErrorResponse("水果名称已存在"));
            }
            fruit.setName(request.getName());
        }



        if (request.getDescription() != null) {
            fruit.setDescription(request.getDescription());
        }

        Fruit saved = fruitRepository.save(fruit);
        logger.info("Successfully updated fruit: {}", saved.getId());
        return ResponseEntity.ok(convertToResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFruit(@PathVariable Long id) {
        logger.info("DELETE /api/admin/fruits/{} - Deleting fruit", id);

        if (!fruitRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        fruitRepository.deleteById(id);
        logger.info("Successfully deleted fruit: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/nutrition")
    public ResponseEntity<?> addNutritionData(@PathVariable Long id, @Valid @RequestBody NutritionDataRequest request) {
        logger.info("POST /api/admin/fruits/{}/nutrition - Adding nutrition data", id);

        Optional<Fruit> fruit = fruitRepository.findById(id);
        if (fruit.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        NutritionData data = new NutritionData();
        data.setFruit(fruit.get());
        data.setComponentName(request.getComponentName());
        data.setComponentValue(request.getComponentValue());

        NutritionData saved = nutritionDataRepository.save(data);
        logger.info("Successfully added nutrition data: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{id}/flavor")
    public ResponseEntity<?> addFlavorData(@PathVariable Long id, @Valid @RequestBody FlavorDataRequest request) {
        logger.info("POST /api/admin/fruits/{}/flavor - Adding flavor data", id);

        Optional<Fruit> fruit = fruitRepository.findById(id);
        if (fruit.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FlavorData data = new FlavorData();
        data.setFruit(fruit.get());
        data.setComponentName(request.getComponentName());
        data.setComponentValue(request.getComponentValue());

        FlavorData saved = flavorDataRepository.save(data);
        logger.info("Successfully added flavor data: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{fruitId}/nutrition/{dataId}")
    public ResponseEntity<Void> deleteNutritionData(@PathVariable Long fruitId, @PathVariable Long dataId) {
        logger.info("DELETE /api/admin/fruits/{}/nutrition/{} - Deleting nutrition data", fruitId, dataId);

        if (!nutritionDataRepository.existsById(dataId)) {
            return ResponseEntity.notFound().build();
        }

        nutritionDataRepository.deleteById(dataId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{fruitId}/flavor/{dataId}")
    public ResponseEntity<Void> deleteFlavorData(@PathVariable Long fruitId, @PathVariable Long dataId) {
        logger.info("DELETE /api/admin/fruits/{}/flavor/{} - Deleting flavor data", fruitId, dataId);

        if (!flavorDataRepository.existsById(dataId)) {
            return ResponseEntity.notFound().build();
        }

        flavorDataRepository.deleteById(dataId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/files")
    public ResponseEntity<?> uploadFile(
            @PathVariable Long id,
            @RequestParam String fileType,
            @RequestParam("file") MultipartFile file) {
        logger.info("POST /api/admin/fruits/{}/files - Uploading file, type: {}", id, fileType);

        if (!fruitRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            FruitFile fruitFile = new FruitFile();
            fruitFile.setFruit(fruitRepository.findById(id).get());
            fruitFile.setFileType(fileType);
            fruitFile.setFileName(file.getOriginalFilename());
            fruitFile.setFilePath(file.getOriginalFilename());
            fruitFile.setFileSize(file.getSize());
            fruitFile.setMimeType(file.getContentType());

            FruitFile saved = fruitFileRepository.save(fruitFile);
            logger.info("Successfully uploaded file: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            logger.error("Failed to upload file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("文件上传失败"));
        }
    }

    private FruitResponse convertToResponse(Fruit fruit) {
        FruitResponse response = new FruitResponse();
        response.setId(fruit.getId());
        response.setName(fruit.getName());
        response.setDescription(fruit.getDescription());
        response.setImagePath(fruit.getImagePath());

        List<NutritionData> nutrition = nutritionDataRepository.findByFruitId(fruit.getId());
        response.setNutritionData(nutrition.stream()
                .map(n -> new FruitResponse.DataItem(n.getComponentName(), n.getComponentValue()))
                .collect(Collectors.toList()));

        List<FlavorData> flavor = flavorDataRepository.findByFruitId(fruit.getId());
        response.setFlavorData(flavor.stream()
                .map(f -> new FruitResponse.DataItem(f.getComponentName(), f.getComponentValue()))
                .collect(Collectors.toList()));

        return response;
    }

    private ErrorResponse createErrorResponse(String message) {
        return new ErrorResponse(message);
    }

    public static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}
