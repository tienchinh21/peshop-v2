package xjanua.backend.dto.variantValue;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.propertyProduct.PropertyProductResponse;
import xjanua.backend.dto.propertyValue.PropertyValueResponseDto;

@Getter
@Setter
public class VariantValueResponseDto {
    private Integer id;
    private PropertyProductResponse propertyProduct;
    private PropertyValueResponseDto propertyValue;
}