package xjanua.backend.dto.Promotion.PromotionGift;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.product.ProductForPromotion;

@Getter
@Setter
public class PromotionGiftResponseDto {
    private String id;
    private ProductForPromotion product;
    private Integer giftQuantity;
    private Boolean isDeleted;
}