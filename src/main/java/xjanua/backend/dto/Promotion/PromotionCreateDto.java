package xjanua.backend.dto.Promotion;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionCreateDto {
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Status is required")
    @Min(value = 0, message = "Status must be greater than 0")
    @Max(value = 2, message = "Status must be less than 2")
    private Integer status;
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private Instant startTime;
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private Instant endTime;
    @NotNull(message = "Total usage limit is required")
    @Min(value = 0, message = "Total usage limit must be greater than 0")
    private Integer totalUsageLimit;
}