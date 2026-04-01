package xjanua.backend.dto.product;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductClient {
    private String id;
    private String name;
    private BigDecimal price;
    private String imgMain;
    private Integer reviewCount;
    private Float reviewPoint;
    private Integer boughtCount;
    private String addressShop;
    private String slug;
    private String shopId;
    private String shopName;
    private Boolean hasPromotion;
    private Boolean hasFlashSale;
    private BigDecimal flashSalePrice;
    private String status;
}