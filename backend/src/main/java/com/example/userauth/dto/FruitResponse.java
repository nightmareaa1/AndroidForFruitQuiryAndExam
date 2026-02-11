package com.example.userauth.dto;

import java.math.BigDecimal;
import java.util.List;

public class FruitResponse {

    private Long id;
    private String name;
    private String description;
    private String imagePath;
    private List<DataItem> nutritionData;
    private List<DataItem> flavorData;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public List<DataItem> getNutritionData() { return nutritionData; }
    public void setNutritionData(List<DataItem> nutritionData) { this.nutritionData = nutritionData; }

    public List<DataItem> getFlavorData() { return flavorData; }
    public void setFlavorData(List<DataItem> flavorData) { this.flavorData = flavorData; }

    public static class DataItem {
        private String componentName;
        private BigDecimal value;

        public DataItem() {}

        public DataItem(String componentName, BigDecimal value) {
            this.componentName = componentName;
            this.value = value;
        }

        public String getComponentName() { return componentName; }
        public void setComponentName(String componentName) { this.componentName = componentName; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
    }
}
