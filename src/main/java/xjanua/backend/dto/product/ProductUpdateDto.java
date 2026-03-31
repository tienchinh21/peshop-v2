package xjanua.backend.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Status is required")
    @Min(value = 0, message = "Status must be greater than 0")
    private Integer status;

    @PositiveOrZero(message = "Weight must be greater than or equal to 0")
    private Integer weight;

    @PositiveOrZero(message = "Height must be greater than or equal to 0")
    private Integer height;

    @PositiveOrZero(message = "Length must be greater than or equal to 0")
    private Integer length;

    @PositiveOrZero(message = "Width must be greater than or equal to 0")
    private Integer width;
}