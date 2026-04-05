package xjanua.backend.dto.tmplCate.child;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmplCateChildUpdateDto {

    @NotNull(message = "Id is required")
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 60, message = "Name must be less than 60 characters")
    private String name;
}
