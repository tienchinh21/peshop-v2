package xjanua.backend.dto.propertyValue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyValueCreateDto {

    @NotBlank(message = "Value is required")
    private String value;

    @NotBlank(message = "Property product ID is required")
    private String propertyProductId;

    @NotNull(message = "Level is required")
    private Integer level;

    private String urlImage;

    @NotNull(message = "Code is required")
    private Integer code;
}