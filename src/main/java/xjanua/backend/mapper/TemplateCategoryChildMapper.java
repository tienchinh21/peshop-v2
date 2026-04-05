package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.tmplCate.child.TmplCateChildResponseDto;
import xjanua.backend.model.TemplateCategoryChild;

@Mapper(componentModel = "spring", uses = { AttributeTemplateMapper.class })
public interface TemplateCategoryChildMapper {
    TmplCateChildResponseDto toDto(TemplateCategoryChild entity);

}
