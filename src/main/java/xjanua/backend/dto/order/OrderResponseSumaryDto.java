package xjanua.backend.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.order.detail.OrderDetailSumaryResponseDto;

@Getter
@Setter
public class OrderResponseSumaryDto {
    private String id;
    private String orderCode;
    private BigDecimal revenue;
    private Integer statusOrder;
    private Instant createdAt;
    private List<OrderDetailSumaryResponseDto> orderDetails;
    private String note;
    private String recipientName;
    private String recipientPhone;
    private Integer paymentMethod;
    private Integer statusPayment;
}