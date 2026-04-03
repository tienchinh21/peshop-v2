package xjanua.backend.dto.Promotion;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftCreateDto;
import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleCreateDto;

@Getter
@Setter
public class PromotionMapEntityCreateDto {
    @Valid
    @NotNull(message = "Promotion create dto is required")
    private PromotionCreateDto promotionCreateDto;
    @Valid
    @NotNull(message = "Promotion gifts is required")
    private List<PromotionGiftCreateDto> promotionGifts;
    @Valid
    @NotNull(message = "Promotion rules is required")
    private List<PromotionRuleCreateDto> promotionRules;
}