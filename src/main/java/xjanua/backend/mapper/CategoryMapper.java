package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.category.CategoryResponseDetailDto;
import xjanua.backend.dto.category.CategoryResponseDto;
import xjanua.backend.model.Category;

@Mapper(componentModel = "spring", uses = { CategoryChildMapper.class })
public interface CategoryMapper {
    CategoryResponseDto toDto(Category entity);

    CategoryResponseDetailDto toDtoDetail(Category entity);
}