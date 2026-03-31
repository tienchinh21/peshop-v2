package xjanua.backend.dto.FlashSale.FlashSaleProduct;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.FlashSale.FlashSaleResponeDto;
import xjanua.backend.dto.product.ProductDtoForBot;

@Getter
@Setter
public class FlashSaleProductResponseDto {
    private String id;
    private ProductDtoForBot product;
    private FlashSaleResponeDto flashSale;
}