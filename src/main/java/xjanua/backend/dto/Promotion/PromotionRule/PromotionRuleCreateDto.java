package xjanua.backend.dto.Promotion.PromotionRule;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionRuleCreateDto {
    @NotNull(message = "Product ID is required")
    private String productId;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
}