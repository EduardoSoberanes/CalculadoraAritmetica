package com.educode.apps.calculadoraaritmetica.models.dtos;

import com.educode.apps.calculadoraaritmetica.models.enums.OperationEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

public class CalculationDTO {

    @JsonProperty("operation")
    private OperationEnum operationEnum;
    private BigDecimal operandA;
    private BigDecimal operandB;

    public CalculationDTO() {
    }

    public CalculationDTO(OperationEnum operationEnum, BigDecimal operandA, BigDecimal operandB) {
        this.operationEnum = operationEnum;
        this.operandA = operandA;
        this.operandB = operandB;
    }

    public OperationEnum getOperationEnum() {
        return operationEnum;
    }

    public void setOperationEnum(OperationEnum operationEnum) {
        this.operationEnum = operationEnum;
    }

    public BigDecimal getOperandA() {
        return operandA;
    }

    public void setOperandA(BigDecimal operandA) {
        this.operandA = operandA;
    }

    public BigDecimal getOperandB() {
        return operandB;
    }

    public void setOperandB(BigDecimal operandB) {
        this.operandB = operandB;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("operationEnum", operationEnum)
                .append("operandA", operandA)
                .append("operandB", operandB)
                .toString();
    }
}
