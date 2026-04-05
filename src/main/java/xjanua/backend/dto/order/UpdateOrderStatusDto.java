package xjanua.backend.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusDto {
    @NotNull(message = "Order ID is required")
    private String orderId;
}