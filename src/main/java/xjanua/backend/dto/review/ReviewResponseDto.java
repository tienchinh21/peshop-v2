package xjanua.backend.dto.review;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.variant.VariantResponseDto;

@Getter
@Setter
public class ReviewResponseDto {
    private Integer id;
    private Integer rating;
    private String content;
    private String replyContent;
    private String urlImg;
    private UserResponseDto user;
    private ProductResponseDto product;
    private VariantResponseDto variant;
    private Instant createdAt;

    @Getter
    @Setter
    public static class UserResponseDto {
        private String name;
        private String avatar;
    }

    @Getter
    @Setter
    public static class ProductResponseDto {
        private String id;
        private String name;
        private String imgMain;
    }
}
