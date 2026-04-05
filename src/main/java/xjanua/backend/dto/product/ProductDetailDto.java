package xjanua.backend.dto.product;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.model.Product;
import xjanua.backend.model.PropertyValue;
import xjanua.backend.model.Variant;
import xjanua.backend.model.VariantValue;

@Getter
@Setter
public class ProductDetailDto {
    private Product product;
    private List<Variant> variants;
    private List<VariantValue> variantValues;
    private PropertyValue propertyValue;
}