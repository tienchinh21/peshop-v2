package xjanua.backend.dto.Promotion.PromotionGift;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionGiftUpdateDto {
    @NotNull(message = "Promotion gift ID is required")
    private String id;
    @NotNull(message = "Product ID is required")
    private String productId;
    @NotNull(message = "Gift quantity is required")
    @Min(value = 1, message = "Gift quantity must be greater than 0")
    private Integer giftQuantity;
}