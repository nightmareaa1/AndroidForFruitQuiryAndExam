package com.example.userauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class NutritionDataRequest {

    @NotBlank(message = "营养成分名称不能为空")
    private String componentName;

    @NotNull(message = "数值不能为空")
    private BigDecimal componentValue;

    private String unit;

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public BigDecimal getComponentValue() { return componentValue; }
    public void setComponentValue(BigDecimal componentValue) { this.componentValue = componentValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
