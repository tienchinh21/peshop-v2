package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleResponseDto;
import xjanua.backend.model.PromotionRule;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface PromotionRuleMapper {
    PromotionRuleResponseDto toDto(PromotionRule entity);
}