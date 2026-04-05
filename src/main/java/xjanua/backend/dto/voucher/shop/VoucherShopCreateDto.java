package xjanua.backend.dto.voucher.shop;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherShopCreateDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    @Size(max = 55, message = "Code must be less than 55 characters")
    private String code;

    @NotNull(message = "Type is required")
    @Min(value = 0, message = "Type must be >= 0")
    @Max(value = 1, message = "Type must be <= 1")
    private Integer type;

    @NotNull(message = "Discount value is required")
    @Min(value = 0, message = "Discount value must be greater than 0")
    private Integer discountValue;

    @Min(value = 0, message = "Max discount amount must be >= 0")
    private Integer maxDiscountAmount;

    @Min(value = 0, message = "Minimum order value must be >= 0")
    @NotNull(message = "Minimum order value is required")
    private Integer minimumOrderValue;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Limit for user is required")
    @Min(value = 1, message = "Limit for user must be at least 1")
    private Integer limitForUser;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private Instant startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private Instant endTime;
}