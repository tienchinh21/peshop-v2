package xjanua.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import xjanua.backend.dto.review.ReviewResponseDto;
import xjanua.backend.model.Review;

@Mapper(componentModel = "spring", uses = { ProductMapper.class, VariantMapper.class })
public interface ReviewMapper {
    @Mapping(target = "user.name", source = "user.name")
    @Mapping(target = "user.avatar", source = "user.avatar")
    @Mapping(target = "product.id", source = "product.id")
    @Mapping(target = "product.name", source = "product.name")
    @Mapping(target = "product.imgMain", source = "product.imgMain")
    ReviewResponseDto toReviewResponseDto(Review review);
}
