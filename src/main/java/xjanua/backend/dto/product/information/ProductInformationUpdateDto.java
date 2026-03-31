package xjanua.backend.dto.product.information;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInformationUpdateDto {
    @NotNull(message = "Product information ID is required")
    private Integer id;
    @NotBlank(message = "Value is required")
    private String value;
}