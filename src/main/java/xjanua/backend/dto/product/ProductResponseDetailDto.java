package xjanua.backend.dto.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xjanua.backend.model.Product;

@Getter
@Setter
@NoArgsConstructor
public class ProductResponseDetailDto {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imgMain;
    private Integer status;
    private Integer classify;
    private Float score;
    private String categoryChildId;
    private String categoryChildName;
    private Integer weight;
    private Integer height;
    private Integer length;
    private Integer width;

    private List<ImageDto> images;
    private List<InformationDto> productInformations;
    private List<OptionDto> options;
    private List<VariantOptimizedDto> variants;

    public ProductResponseDetailDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.imgMain = product.getImgMain();
        this.status = product.getStatus();
        this.classify = product.getClassify();
        this.score = product.getScore();
        this.categoryChildId = product.getCategoryChild().getId();
        this.categoryChildName = product.getCategoryChild().getName();
        this.weight = product.getWeight();
        this.height = product.getHeight();
        this.length = product.getLength();
        this.width = product.getWidth();

        this.images = product.getImages().stream()
                .map(image -> new ImageDto(image.getId(), image.getUrl(),
                        image.getSortOrder()))
                .collect(Collectors.toList());

        this.productInformations = product.getProductInformations().stream()
                .map(information -> new InformationDto(information.getId(),
                        information.getName(),
                        information.getValue()))
                .collect(Collectors.toList());

        // Group property values by property product name and remove duplicates
        Map<String, List<PropertyValueDto>> groupedOptions = product.getVariants().stream()
                .flatMap(variant -> variant.getVariantValues().stream())
                .collect(Collectors.groupingBy(
                        variantValue -> variantValue.getPropertyProduct().getName(),
                        Collectors.mapping(
                                variantValue -> new PropertyValueDto(
                                        variantValue.getPropertyValue().getId(),
                                        variantValue.getPropertyValue().getValue(),
                                        variantValue.getPropertyValue().getImgUrl()),
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .distinct()
                                                .collect(Collectors.toList())))));

        this.options = groupedOptions.entrySet().stream()
                .map(entry -> {
                    // Get level from the first PropertyValue in the group
                    Integer level = product.getVariants().stream()
                            .flatMap(variant -> variant.getVariantValues().stream())
                            .filter(variantValue -> variantValue.getPropertyProduct().getName().equals(entry.getKey()))
                            .map(variantValue -> variantValue.getPropertyValue().getLevel())
                            .findFirst()
                            .orElse(0);
                    return new OptionDto(entry.getKey(), level, entry.getValue());
                })
                .collect(Collectors.toList());

        this.variants = product.getVariants().stream()
                .map(variant -> new VariantOptimizedDto(
                        variant.getId(),
                        variant.getPrice(),
                        variant.getQuantity(),
                        variant.getVariantValues().stream()
                                .map(variantValue -> variantValue.getPropertyValue().getId())
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    private class OptionDto {
        private String name;
        private Integer level;
        private List<PropertyValueDto> values;

        public OptionDto(String name, Integer level, List<PropertyValueDto> values) {
            this.name = name;
            this.level = level;
            this.values = values;
        }
    }

    @Getter
    @Setter
    private class PropertyValueDto {
        private String id;
        private String value;
        private String imgUrl;

        public PropertyValueDto(String id, String value, String imgUrl) {
            this.id = id;
            this.value = value;
            this.imgUrl = imgUrl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            PropertyValueDto that = (PropertyValueDto) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Getter
    @Setter
    private class VariantOptimizedDto {
        private Integer id;
        private BigDecimal price;
        private Integer quantity;
        private List<String> propertyValueIds;

        public VariantOptimizedDto(Integer id, BigDecimal price, Integer quantity,
                List<String> propertyValueIds) {
            this.id = id;
            this.price = price;
            this.quantity = quantity;
            this.propertyValueIds = propertyValueIds;
        }
    }

    @Getter
    @Setter
    private class InformationDto {
        private Integer id;
        private String name;
        private String value;

        public InformationDto(Integer id, String name, String value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }
    }

    @Getter
    @Setter
    private class ImageDto {
        private String id;
        private String url;
        private Integer sortOrder;

        public ImageDto(String id, String url, Integer sortOrder) {
            this.id = id;
            this.url = url;
            this.sortOrder = sortOrder;
        }
    }
}
