package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.attributeTemplate.AttributeTemplateResponseDto;
import xjanua.backend.model.AttributeTemplate;

@Mapper(componentModel = "spring")
public interface AttributeTemplateMapper {
    AttributeTemplateResponseDto toDto(AttributeTemplate entity);
}