package xjanua.backend.dto.category.child;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryChildCreateDto {

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotBlank(message = "Name is required")
    @Size(max = 60, message = "Name must be less than 60 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}
