package xjanua.backend.dto.order.detail;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.product.NameAndImageResponseDto;

@Getter
@Setter
public class OrderDetailSumaryResponseDto {
    private NameAndImageResponseDto product;
    private List<String> propertyValueNames;
    private Integer quantity;
}