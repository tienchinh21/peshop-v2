package xjanua.backend.service.shop;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.review.ReviewReplyDto;
import xjanua.backend.dto.review.ReviewResponseDto;
import xjanua.backend.mapper.ReviewMapper;
import xjanua.backend.model.Review;
import xjanua.backend.repository.ReviewRepo;
import xjanua.backend.util.PaginationUtil;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final ShopService shopService;
    private final ReviewMapper reviewMapper;

    public PaginationDTO.Response fetchAllReviewByShop(Specification<Review> specification, Pageable pageable) {
        String shopId = shopService.fetchByUserLogin().getId();

        Specification<Review> shopSpec = (root, query, cb) -> cb.equal(root.get("shop").get("id"), shopId);

        Specification<Review> finalSpec = (specification == null) ? shopSpec : specification.and(shopSpec);

        PaginationDTO.Response response = new PaginationDTO.Response();
        Page<Review> reviews = this.reviewRepo.findAll(finalSpec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(reviews, pageable);

        List<ReviewResponseDto> reviewDTOs = reviews.getContent()
                .stream()
                .map(reviewMapper::toReviewResponseDto)
                .collect(Collectors.toList());

        response.setInfo(info);
        response.setResponse(reviewDTOs);
        return response;
    }

    @Transactional
    public void replyToReview(Integer reviewId, ReviewReplyDto replyDto) {
        String shopId = shopService.fetchByUserLogin().getId();
        
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.REVIEW_NOT_FOUND_MESSAGE));
        
        // Kiểm tra xem review có thuộc về shop này không
        if (review.getShop() == null || !review.getShop().getId().equals(shopId)) {
            throw new ResourceNotFoundException(ResponseConstants.REVIEW_NOT_FOUND_MESSAGE);
        }
        
        review.setReplyContent(replyDto.getReplyContent());
        reviewRepo.save(review);
    }
}
