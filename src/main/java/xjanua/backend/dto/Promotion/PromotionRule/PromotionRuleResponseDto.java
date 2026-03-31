package xjanua.backend.dto.Promotion.PromotionRule;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.product.ProductForPromotion;

@Getter
@Setter
public class PromotionRuleResponseDto {
    private String id;
    private ProductForPromotion product;
    private Integer quantity;
}