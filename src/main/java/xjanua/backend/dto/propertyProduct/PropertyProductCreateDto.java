package xjanua.backend.dto.propertyProduct;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyProductCreateDto {
    @Size(max = 60, message = "Name must be less than 60 characters")
    private String name;
}