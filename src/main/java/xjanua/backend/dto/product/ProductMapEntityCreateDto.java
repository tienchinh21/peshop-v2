package xjanua.backend.dto.product;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.image.ImagePrdCreateDto;
import xjanua.backend.dto.product.information.ProductInformationCreateDto;
import xjanua.backend.dto.propertyValue.PropertyValueCreateDto;
import xjanua.backend.dto.variant.VariantCreateDtoMapWithKeys;

@Getter
@Setter
public class ProductMapEntityCreateDto {

    @Valid
    @NotNull(message = "Product information is required")
    private ProductCreateDto product;

    @Valid
    @NotEmpty(message = "Images Product is required")
    private List<ImagePrdCreateDto> ImagesProduct;

    @Valid
    @NotEmpty(message = "Product informations is required")
    private List<ProductInformationCreateDto> productInformations;

    @Valid
    private List<PropertyValueCreateDto> propertyValues;

    @Valid
    @NotNull(message = "Variants are required")
    private List<VariantCreateDtoMapWithKeys> variants;
}