package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.propertyProduct.PropertyProductResponse;
import xjanua.backend.model.PropertyProduct;

@Mapper(componentModel = "spring")
public interface PropertyProductMapper {
    PropertyProductResponse toDto(PropertyProduct entity);
}