package xjanua.backend.dto.Promotion;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftUpdateDto;
import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleUpdateDto;

@Getter
@Setter
public class PromotionMapEntityUpdateDto {
    @Valid
    @NotNull(message = "Promotion update dto is required")
    private PromotionUpdateDto promotionUpdateDto;
    @Valid
    @NotNull(message = "Promotion gifts is required")
    private List<PromotionGiftUpdateDto> promotionGifts;
    @Valid
    @NotNull(message = "Promotion rules is required")
    private List<PromotionRuleUpdateDto> promotionRules;
}