package xjanua.backend.dto.Promotion;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionResponseDto {
    private String id;
    private String name;
    private Integer status;
    private Instant startTime;
    private Instant endTime;
    private Integer totalUsageLimit;
}