package xjanua.backend.dto.FlashSale;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashSaleResponeDto {
    private String id;
    private Instant startTime;
    private Instant endTime;
    private Integer status;
    private Long productCount;
    private Long soldQuantity;
}