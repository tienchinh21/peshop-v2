package xjanua.backend.dto.product;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.category.CategoryResponseDto;
import xjanua.backend.dto.category.child.CategoryChildResponseDto;
import xjanua.backend.dto.propertyValue.PropertyValueResponseDto;

@Getter
@Setter
public class ProductSummaryByShopDto {
    private String id;
    private String name;
    private String imgMain;
    private BigDecimal price;
    private Integer status;
    private Integer classify;
    private Float score;
    private Integer boughtCount;
    private Float reviewPoint;
    private String slug;
    private Integer likeCount;
    private Integer viewCount;
    private Integer reviewCount;
    CategoryResponseDto category;
    CategoryChildResponseDto categoryChild;
    List<PropertyValueResponseDto> propertyValues;
    List<VariantOptimizedDto> variants;

    @Getter
    @Setter
    public static class VariantOptimizedDto {
        private Integer id;
        private BigDecimal price;
        private Integer quantity;
        private List<String> propertyValueIds;
    }
}