package xjanua.backend.dto.FlashSale;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.product.ProductDtoForBot;

@Getter
@Setter
public class FlashSaleGroupDto {
    private FlashSaleResponeDto flashSale;
    private List<ProductDtoForBot> products;
}
