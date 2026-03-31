package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.variant.VariantForPromotionDto;
import xjanua.backend.dto.variant.VariantResponseDto;
import xjanua.backend.model.Variant;

@Mapper(componentModel = "spring", uses = { VariantValueMapper.class })
public interface VariantMapper {
    VariantResponseDto toDto(Variant entity);

    VariantForPromotionDto toVariantForPromotionDto(Variant entity);
}