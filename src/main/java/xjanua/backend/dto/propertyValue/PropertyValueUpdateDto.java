package xjanua.backend.dto.propertyValue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyValueUpdateDto {
    @NotNull(message = "Property value ID is required")
    private String propertyValueId;

    @NotBlank(message = "Value is required")
    private String value;

    private String urlImage;
}