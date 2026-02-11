package com.example.userauth.controller;

import com.example.userauth.entity.Fruit;
import com.example.userauth.entity.FruitData;
import com.example.userauth.entity.FruitDataField;
import com.example.userauth.repository.FruitDataFieldRepository;
import com.example.userauth.repository.FruitDataRepository;
import com.example.userauth.repository.FruitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fruit-data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FruitDataController {

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private FruitDataRepository fruitDataRepository;

    @Autowired
    private FruitDataFieldRepository fieldRepository;

    @GetMapping("/fruits")
    public ResponseEntity<List<FruitOption>> getFruits() {
        List<Fruit> fruits = fruitRepository.findAll();
        List<FruitOption> options = fruits.stream()
                .map(f -> new FruitOption(f.getId(), f.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(options);
    }

    @GetMapping("/fields/{dataType}")
    public ResponseEntity<List<FieldOption>> getFields(@PathVariable String dataType) {
        List<FruitDataField> fields = fieldRepository.findByFieldTypeAndIsActiveTrueOrderByDisplayOrder(dataType);
        List<FieldOption> options = fields.stream()
                .map(f -> new FieldOption(f.getId(), f.getFieldName(), f.getFieldUnit()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(options);
    }

    @GetMapping("/query")
    public ResponseEntity<FruitDataResponse> query(
            @RequestParam String fruit,
            @RequestParam String dataType) {
        Optional<Fruit> fruitOpt = fruitRepository.findByName(fruit);
        if (fruitOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Fruit fruitEntity = fruitOpt.get();
        Optional<FruitData> dataOpt = fruitDataRepository.findByFruitNameAndDataType(fruitEntity.getName(), dataType);

        FruitDataResponse response = new FruitDataResponse();
        response.setFruitName(fruitEntity.getName());
        response.setDataType(dataType);

        if (dataOpt.isPresent()) {
            response.setData(dataOpt.get().getDataValues());
        } else {
            response.setData(new HashMap<>());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/{dataType}")
    public ResponseEntity<List<FruitDataResponse>> getAllData(@PathVariable String dataType) {
        List<Fruit> fruits = fruitRepository.findAll();
        List<FruitDataResponse> results = new ArrayList<>();

        for (Fruit fruit : fruits) {
            Optional<FruitData> dataOpt = fruitDataRepository.findByFruitNameAndDataType(fruit.getName(), dataType);

            FruitDataResponse response = new FruitDataResponse();
            response.setFruitName(fruit.getName());
            response.setDataType(dataType);

            if (dataOpt.isPresent()) {
                response.setData(dataOpt.get().getDataValues());
            } else {
                response.setData(new HashMap<>());
            }

            results.add(response);
        }

        return ResponseEntity.ok(results);
    }

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

    public static class FruitDataResponse {
        private String fruitName;
        private String dataType;
        private Map<String, Double> data;

        public String getFruitName() { return fruitName; }
        public void setFruitName(String fruitName) { this.fruitName = fruitName; }

        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }

        public Map<String, Double> getData() { return data; }
        public void setData(Map<String, Double> data) { this.data = data; }
    }
}
