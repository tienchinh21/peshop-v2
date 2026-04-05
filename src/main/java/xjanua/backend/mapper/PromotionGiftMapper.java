package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftResponseDto;
import xjanua.backend.model.PromotionGift;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface PromotionGiftMapper {
    PromotionGiftResponseDto toDto(PromotionGift entity);
}