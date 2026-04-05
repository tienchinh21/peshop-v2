package xjanua.backend.dto.order;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherOrderResponseDto {
    private String id;
    private String orderCode;
    private BigDecimal originalPrice;
    private BigDecimal shopVoucherDiscount;
    private Integer statusOrder;
    private Instant createdAt;
}