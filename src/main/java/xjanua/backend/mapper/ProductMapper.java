package xjanua.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import xjanua.backend.dto.product.NameAndImageResponseDto;
import xjanua.backend.dto.product.ProductDtoForBot;
import xjanua.backend.dto.product.ProductForPromotion;
import xjanua.backend.dto.product.ProductSummaryByShopDto;
import xjanua.backend.dto.propertyValue.PropertyValueResponseDto;
import xjanua.backend.model.Product;
import xjanua.backend.model.PropertyValue;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class, CategoryChildMapper.class,
        PropertyValueMapper.class })
public interface ProductMapper {
    ProductSummaryByShopDto toProductSummaryByShopDto(Product product);

    ProductForPromotion toProductForPromotion(Product product);

    NameAndImageResponseDto toNameAndImageResponseDto(Product product);

    ProductDtoForBot toProductDtoForBot(Product product);

    @AfterMapping
    default void mapPropertyValues(Product product,
            @MappingTarget ProductSummaryByShopDto dto) {

        if (product.getVariants() == null || product.getVariants().isEmpty())
            return;

        // Lấy tất cả PropertyValue từ các Variant (loại bỏ trùng lặp)
        List<PropertyValue> propertyValues = product.getVariants().stream()
                .flatMap(variant -> variant.getVariantValues().stream())
                .map(variantValue -> variantValue.getPropertyValue())
                .distinct() // Loại bỏ trùng lặp
                .collect(Collectors.toList());

        // Map sang DTO
        List<PropertyValueResponseDto> propertyValueDtos = propertyValues.stream()
                .map(pv -> {
                    PropertyValueResponseDto pvDto = new PropertyValueResponseDto();
                    pvDto.setId(pv.getId());
                    pvDto.setValue(pv.getValue());
                    pvDto.setImgUrl(pv.getImgUrl());
                    pvDto.setLevel(pv.getLevel());
                    return pvDto;
                })
                .collect(Collectors.toList());

        dto.setPropertyValues(propertyValueDtos);
    }
}