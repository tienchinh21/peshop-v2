package xjanua.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import xjanua.backend.dto.order.detail.OrderDetailSumaryResponseDto;
import xjanua.backend.model.OrderDetail;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface OrderDetailMapper {
    @Mapping(target = "propertyValueNames", source = "propertyValueNames")
    OrderDetailSumaryResponseDto toOrderDetailSumaryResponseDto(OrderDetail orderDetail,
            List<String> propertyValueNames);
}
