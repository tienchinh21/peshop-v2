package xjanua.backend.dto.voucher.shop;

import java.time.Instant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherShopUpdateDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Discount value is required")
    @Min(value = 0, message = "Discount value must be greater than 0")
    private Integer discountValue;

    @Min(value = 0, message = "Max discount amount must be >= 0")
    private Integer maxDiscountAmount;

    @NotNull(message = "Minimum order value is required")
    @Min(value = 0, message = "Minimum order value must be >= 0")
    private Integer minimumOrderValue;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;
}