package xjanua.backend.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewReplyDto {
    @NotBlank(message = "Reply content is required")
    private String replyContent;
}




