package xjanua.backend.dto.attributeTemplate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributeTemplateCreateDto {

    @NotBlank(message = "Name is required")
    @Size(max = 45, message = "Name must be less than 45 characters")
    private String name;

    private Integer templateCategoryId;

    private Integer templateCategoryChildId;
}