package xjanua.backend.dto.voucher.shop.dash;

import java.math.BigDecimal;
import java.time.Instant;

public interface OrderMetricsViewDto {
    Instant getCreatedAt();

    BigDecimal getOriginalPrice();

    BigDecimal getShopVoucherDiscount();

    String getUserId();

    int getStatusOrder();
}