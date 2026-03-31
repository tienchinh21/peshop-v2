package xjanua.backend.dto.FlashSale.FlashSaleProduct;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashSaleProductCreateDto {
    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Percent decrease is required")
    @Min(value = 0, message = "Percent decrease must be greater than 0")
    @Max(value = 100, message = "Percent decrease must be less than 100")
    private Integer percentDecrease;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Order limit is required")
    @Min(value = 0, message = "Order limit must be greater than 0")
    private Integer orderLimit;
}