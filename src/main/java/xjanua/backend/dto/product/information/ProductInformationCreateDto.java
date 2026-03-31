package xjanua.backend.dto.product.information;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInformationCreateDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Value is required")
    @Size(max = 255, message = "Value must be less than 255 characters")
    private String value;
}