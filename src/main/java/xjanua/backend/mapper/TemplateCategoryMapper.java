package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.tmplCate.TmplCateResponseDto;
import xjanua.backend.model.TemplateCategory;

@Mapper(componentModel = "spring", uses = { AttributeTemplateMapper.class })
public interface TemplateCategoryMapper {
    TmplCateResponseDto toDto(TemplateCategory entity);
}