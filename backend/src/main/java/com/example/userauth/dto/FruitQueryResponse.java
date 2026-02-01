package com.example.userauth.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for fruit nutrition and flavor queries.
 * Contains fruit name, query type, and data list.
 */
public class FruitQueryResponse {

    private String fruit;
    private String type;
    private List<QueryDataItem> data;

    // Default constructor
    public FruitQueryResponse() {
    }

    // Constructor with all fields
    public FruitQueryResponse(String fruit, String type, List<QueryDataItem> data) {
        this.fruit = fruit;
        this.type = type;
        this.data = data;
    }

    // Getters and Setters
    public String getFruit() {
        return fruit;
    }

    public void setFruit(String fruit) {
        this.fruit = fruit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<QueryDataItem> getData() {
        return data;
    }

    public void setData(List<QueryDataItem> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FruitQueryResponse{" +
                "fruit='" + fruit + '\'' +
                ", type='" + type + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * Inner class representing a single data item in the query result.
     * Contains component name and value.
     */
    public static class QueryDataItem {
        private String componentName;
        private BigDecimal value;

        // Default constructor
        public QueryDataItem() {
        }

        // Constructor with all fields
        public QueryDataItem(String componentName, BigDecimal value) {
            this.componentName = componentName;
            this.value = value;
        }

        // Getters and Setters
        public String getComponentName() {
            return componentName;
        }

        public void setComponentName(String componentName) {
            this.componentName = componentName;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "QueryDataItem{" +
                    "componentName='" + componentName + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}