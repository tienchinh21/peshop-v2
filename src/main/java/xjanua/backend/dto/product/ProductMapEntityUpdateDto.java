package xjanua.backend.dto.product;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.image.ImagePrdUpdateDto;
import xjanua.backend.dto.product.information.ProductInformationUpdateDto;
import xjanua.backend.dto.propertyValue.PropertyValueUpdateDto;
import xjanua.backend.dto.variant.VariantUpdateDto;

@Getter
@Setter
public class ProductMapEntityUpdateDto {
    @Valid
    @NotNull(message = "Product is required")
    private ProductUpdateDto product;

    @Valid
    @NotEmpty(message = "Images Product is required")
    private List<ImagePrdUpdateDto> ImagesProduct;

    @Valid
    @NotEmpty(message = "Product informations is required")
    private List<ProductInformationUpdateDto> productInformations;

    @Valid
    private List<PropertyValueUpdateDto> propertyValues;

    @Valid
    @NotEmpty(message = "Variants is required")
    private List<VariantUpdateDto> variants;
}