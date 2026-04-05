package xjanua.backend.dto.voucher.shop;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherSummaryDto {
    private String id;
    private String code;
    private String name;
    private Integer type;
    private BigDecimal discountValue;
    private Integer quantity;
    private Integer quantityUsed;
    private Instant startTime;
    private Instant endTime;
    private Integer status;
}