package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.variantValue.VariantValueResponseDto;
import xjanua.backend.model.VariantValue;

@Mapper(componentModel = "spring", uses = { PropertyProductMapper.class, PropertyValueMapper.class })
public interface VariantValueMapper {
    VariantValueResponseDto toDto(VariantValue entity);
}
