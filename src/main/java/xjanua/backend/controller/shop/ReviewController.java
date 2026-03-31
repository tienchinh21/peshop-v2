package xjanua.backend.controller.shop;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.review.ReviewReplyDto;
import xjanua.backend.model.Review;
import xjanua.backend.service.shop.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/reviews")
@PreAuthorize("hasAuthority('Shop')")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PaginationDTO.Response> getAllReviews(
            @Filter Specification<Review> spec,
            Pageable pageable) {
        PaginationDTO.Response response = reviewService.fetchAllReviewByShop(spec, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<RestResponse<Void>> replyToReview(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewReplyDto replyDto) {
        reviewService.replyToReview(id, replyDto);
        return ResponseEntity.noContent().build();
    }
}