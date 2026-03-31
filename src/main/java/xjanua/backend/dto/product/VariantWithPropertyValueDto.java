package xjanua.backend.dto.product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.propertyValue.PropertyValueCreateDto;

@Getter
@Setter
public class VariantWithPropertyValueDto {

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Status is required")
    @Min(value = 0, message = "Status must be greater than 0")
    private Integer status;

    List<PropertyValueCreateDto> propertyValues;
}