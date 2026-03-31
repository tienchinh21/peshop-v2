package xjanua.backend.dto.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusForHangDto {
    @NotNull(message = "ID is required")
    private String id;

    @NotNull(message = "Status is required")
    @Min(value = 1, message = "Status must be greater than 1")
    @Max(value = 2, message = "Status must be less than 2")
    private Integer status;
}