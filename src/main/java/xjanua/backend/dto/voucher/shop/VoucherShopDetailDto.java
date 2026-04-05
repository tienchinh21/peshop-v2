package xjanua.backend.dto.voucher.shop;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherShopDetailDto {
    private String id;
    private String code;
    private String name;
    private Integer type;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minimumOrderValue;
    private Integer quantity;
    private Integer quantityUsed;
    private Integer limitForUser;
    private Instant startTime;
    private Instant endTime;
    private Integer status;
}