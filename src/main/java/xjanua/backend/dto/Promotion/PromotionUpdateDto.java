package xjanua.backend.dto.Promotion;

import java.time.Instant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionUpdateDto {
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Start time is required")
    private Instant startTime;
    @NotNull(message = "End time is required")
    private Instant endTime;
    @NotNull(message = "Total usage limit is required")
    @Min(value = 0, message = "Total usage limit must be greater than 0")
    private Integer totalUsageLimit;
}