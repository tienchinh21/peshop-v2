package xjanua.backend.dto.variant;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariantUpdateDto {

    @NotNull(message = "Variant ID is required")
    private Integer variantId;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Status is required")
    @Min(value = 0, message = "Status must be greater than 0")
    private Integer status;
}