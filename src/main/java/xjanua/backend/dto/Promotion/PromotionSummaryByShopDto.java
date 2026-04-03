package xjanua.backend.dto.Promotion;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftResponseDto;
import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleResponseDto;

@Getter
@Setter
public class PromotionSummaryByShopDto {
    private String id;
    private String name;
    private Integer status;
    private Instant startTime;
    private Instant endTime;
    private Integer totalUsageLimit;
    private List<PromotionGiftResponseDto> gifts;
    private List<PromotionRuleResponseDto> rules;
}