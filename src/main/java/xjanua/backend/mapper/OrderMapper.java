package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.order.OrderResponseSumaryDto;
import xjanua.backend.dto.order.VoucherOrderResponseDto;
import xjanua.backend.model.Order;

@Mapper(componentModel = "spring", uses = { OrderDetailMapper.class })
public interface OrderMapper {
    VoucherOrderResponseDto toVoucherOrderResponseDto(Order order);

    OrderResponseSumaryDto toOrderResponseSumaryDto(Order order);
}