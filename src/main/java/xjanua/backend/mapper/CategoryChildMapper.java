package xjanua.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import xjanua.backend.dto.category.child.CategoryChildResponseDto;
import xjanua.backend.dto.category.child.CategoryChildResponseDtoDetail;
import xjanua.backend.model.CategoryChild;

@Mapper(componentModel = "spring", uses = { TemplateCategoryChildMapper.class, TemplateCategoryMapper.class })
public interface CategoryChildMapper {
    CategoryChildResponseDto toDto(CategoryChild entity);

    @Mapping(source = "category.templateCategories", target = "templateCategories")
    CategoryChildResponseDtoDetail toDtoDetail(CategoryChild entity);
}