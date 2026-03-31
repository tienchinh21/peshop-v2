package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.propertyValue.PropertyValueResponseDto;
import xjanua.backend.model.PropertyValue;

@Mapper(componentModel = "spring")
public interface PropertyValueMapper {
    PropertyValueResponseDto toDto(PropertyValue entity);
}
