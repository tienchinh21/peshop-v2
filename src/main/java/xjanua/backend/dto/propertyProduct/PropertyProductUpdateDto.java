package xjanua.backend.dto.propertyProduct;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyProductUpdateDto {
    @NotBlank(message = "Property product ID is required")
    private String id;

    @Size(max = 60, message = "Name must be less than 60 characters")
    private String name;
}
