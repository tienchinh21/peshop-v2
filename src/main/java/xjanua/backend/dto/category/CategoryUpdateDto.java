package xjanua.backend.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateDto {
    @NotBlank(message = "Name is required")
    @Size(max = 60, message = "Name must be less than 60 characters")
    private String name;
}
