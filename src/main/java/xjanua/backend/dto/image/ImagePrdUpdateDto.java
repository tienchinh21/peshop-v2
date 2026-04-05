package xjanua.backend.dto.image;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImagePrdUpdateDto {
    private String id;
    @NotBlank(message = "URL image is required")
    private String urlImage;
    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be greater than or equal to 0")
    private Integer sortOrder;
}