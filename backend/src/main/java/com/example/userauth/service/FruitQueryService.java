package com.example.userauth.service;

import com.example.userauth.dto.FruitQueryResponse;
import com.example.userauth.entity.Fruit;
import com.example.userauth.entity.FlavorData;
import com.example.userauth.entity.NutritionData;
import com.example.userauth.repository.FlavorDataRepository;
import com.example.userauth.repository.FruitRepository;
import com.example.userauth.repository.NutritionDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for fruit nutrition and flavor queries.
 * Handles business logic for querying fruit data by type and name.
 */
@Service
public class FruitQueryService {

    private static final Logger logger = LoggerFactory.getLogger(FruitQueryService.class);

    private final FruitRepository fruitRepository;
    private final NutritionDataRepository nutritionDataRepository;
    private final FlavorDataRepository flavorDataRepository;

    @Autowired
    public FruitQueryService(FruitRepository fruitRepository,
                           NutritionDataRepository nutritionDataRepository,
                           FlavorDataRepository flavorDataRepository) {
        this.fruitRepository = fruitRepository;
        this.nutritionDataRepository = nutritionDataRepository;
        this.flavorDataRepository = flavorDataRepository;
    }

    /**
     * Query fruit data by type and fruit name.
     * 
     * @param type the query type ("nutrition" or "flavor")
     * @param fruitName the fruit name
     * @return FruitQueryResponse containing the query results
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if fruit is not found
     */
    public FruitQueryResponse queryFruitData(String type, String fruitName) {
        logger.info("Querying fruit data: type={}, fruit={}", type, fruitName);

        // Validate parameters
        validateQueryParameters(type, fruitName);

        // Validate query type first
        if (!"nutrition".equalsIgnoreCase(type) && !"flavor".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Invalid query type: " + type + ". Must be 'nutrition' or 'flavor'");
        }

        // Check if fruit exists (support both Chinese and English names)
        Optional<Fruit> fruitOptional = fruitRepository.findByName(fruitName);
        if (fruitOptional.isEmpty()) {
            fruitOptional = fruitRepository.findByEnName(fruitName);
        }
        if (fruitOptional.isEmpty()) {
            logger.warn("Fruit not found: {}", fruitName);
            throw new RuntimeException("Fruit not found: " + fruitName);
        }

        Fruit fruit = fruitOptional.get();
        
        // Query data based on type
        FruitQueryResponse response;
        if ("nutrition".equalsIgnoreCase(type)) {
            response = queryNutritionData(fruit);
        } else {
            response = queryFlavorData(fruit);
        }

        logger.info("Successfully queried fruit data: type={}, fruit={}, dataCount={}", 
                   type, fruitName, response.getData().size());
        return response;
    }

    /**
     * Validate query parameters.
     * 
     * @param type the query type
     * @param fruitName the fruit name
     * @throws IllegalArgumentException if parameters are invalid
     */
    private void validateQueryParameters(String type, String fruitName) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Query type cannot be null or empty");
        }
        if (fruitName == null || fruitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Fruit name cannot be null or empty");
        }
    }

    /**
     * Query nutrition data for a fruit.
     * 
     * @param fruit the fruit entity
     * @return FruitQueryResponse with nutrition data
     */
    private FruitQueryResponse queryNutritionData(Fruit fruit) {
        List<NutritionData> nutritionDataList = nutritionDataRepository.findByFruitId(fruit.getId());
        
        List<FruitQueryResponse.QueryDataItem> dataItems = nutritionDataList.stream()
                .map(data -> new FruitQueryResponse.QueryDataItem(
                        data.getComponentName(),
                        data.getComponentValue()
                ))
                .collect(Collectors.toList());

        return new FruitQueryResponse(fruit.getName(), "nutrition", dataItems);
    }

    /**
     * Query flavor data for a fruit.
     * 
     * @param fruit the fruit entity
     * @return FruitQueryResponse with flavor data
     */
    private FruitQueryResponse queryFlavorData(Fruit fruit) {
        List<FlavorData> flavorDataList = flavorDataRepository.findByFruitId(fruit.getId());
        
        List<FruitQueryResponse.QueryDataItem> dataItems = flavorDataList.stream()
                .map(data -> new FruitQueryResponse.QueryDataItem(
                        data.getComponentName(),
                        data.getComponentValue()
                ))
                .collect(Collectors.toList());

        return new FruitQueryResponse(fruit.getName(), "flavor", dataItems);
    }

    /**
     * Check if a fruit exists by name.
     * 
     * @param fruitName the fruit name
     * @return true if fruit exists, false otherwise
     */
    public boolean fruitExists(String fruitName) {
        if (fruitName == null || fruitName.trim().isEmpty()) {
            return false;
        }
        return fruitRepository.existsByName(fruitName);
    }

    /**
     * Get all available fruits.
     * 
     * @return list of all fruits
     */
    public List<Fruit> getAllFruits() {
        return fruitRepository.findAll();
    }
}