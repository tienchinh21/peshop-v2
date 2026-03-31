package xjanua.backend.dto.FlashSale;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashSaleDetailDto {
    private String id;
    private List<ProductDetailDto> products;

    @Getter
    @Setter
    public static class ProductDetailDto {
        private String id;
        private String name;
        private String imgMain;
        private Long soldQuantity;
    }
}



