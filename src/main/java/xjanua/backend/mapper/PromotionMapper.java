package xjanua.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import xjanua.backend.dto.Promotion.PromotionResponseDto;
import xjanua.backend.dto.Promotion.PromotionSummaryByShopDto;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftResponseDto;
import xjanua.backend.model.Promotion;

@Mapper(componentModel = "spring", uses = { PromotionGiftMapper.class, PromotionRuleMapper.class })
public interface PromotionMapper {

    PromotionResponseDto toDto(Promotion entity);

    PromotionSummaryByShopDto toPromotionSummaryByShopDto(Promotion entity);

    @AfterMapping
    default void filterDeletedGifts(@MappingTarget PromotionSummaryByShopDto dto) {
        if (dto.getGifts() != null) {
            List<PromotionGiftResponseDto> filteredGifts = dto.getGifts().stream()
                    .filter(g -> g.getIsDeleted() == null || !g.getIsDeleted())
                    .collect(Collectors.toList());
            dto.setGifts(filteredGifts);
        }
    }
}