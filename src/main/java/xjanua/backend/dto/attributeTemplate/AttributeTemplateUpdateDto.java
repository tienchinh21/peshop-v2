package xjanua.backend.dto.attributeTemplate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributeTemplateUpdateDto {

    @NotNull(message = "ID is required")
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 45, message = "Name must be less than 45 characters")
    private String name;
}
