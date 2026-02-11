package com.example.userauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FlavorDataRequest {

    @NotBlank(message = "风味指标名称不能为空")
    private String componentName;

    @NotNull(message = "评分不能为空")
    private BigDecimal componentValue;

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public BigDecimal getComponentValue() { return componentValue; }
    public void setComponentValue(BigDecimal componentValue) { this.componentValue = componentValue; }
}
