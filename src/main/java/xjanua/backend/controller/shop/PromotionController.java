package xjanua.backend.controller.shop;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.Promotion.PromotionMapEntityCreateDto;
import xjanua.backend.dto.Promotion.PromotionMapEntityUpdateDto;
import xjanua.backend.dto.Promotion.PromotionResponseDto;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftCreateDto;
import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleCreateDto;
import xjanua.backend.dto.product.UpdateStatusForHangDto;
import xjanua.backend.mapper.PromotionMapper;
import xjanua.backend.model.Promotion;
import xjanua.backend.service.shop.PromotionService;

@RestController
@RequestMapping("/shop/promotion")
@PreAuthorize("hasAuthority('Shop')")
@RequiredArgsConstructor
public class PromotionController {
  private final PromotionService PromotionService;
  private final PromotionMapper promotionMapper;

  @Value("${api.key}")
  private String apiKey;

  @Operation(description = """
      Trả về danh sách khuyến mãi (promotion) của shop có thể được lọc, phân trang và sắp xếp.

      **Tham số:**
      - **page**: Số trang (bắt đầu từ 0)
      - **size**: Số phần tử mỗi trang
      - **sort**: Dạng `property,(asc|desc)` — ví dụ: `startTime,desc`
      - **filter**: Biểu thức lọc theo cú pháp Spring Filter.

      **Cách sử dụng filter (Spring Filter):**
      - **Toán tử so sánh:** `:`, `>`, `>=`, `<`, `<=`
        > Ví dụ: `status : 1`, `totalUsageLimit >= 100`
      - **Toán tử logic:** `and`, `or`, `not`
        > Ví dụ: `status : 1 and totalUsageLimit > 0`
      - **So khớp chuỗi:** `~` (like), `~~` (like không phân biệt hoa thường)
        > Ví dụ: `name ~~ 'FLASH'`
        > Nếu muốn so khớp **chính xác toàn bộ chuỗi**, hãy dùng `:` thay vì `~`

      **Ví dụ sử dụng:**
      1. Lọc theo tên promotion:
         ```
         /shop/promotion?page=0&size=10&filter=name ~~ 'SALE'
         ```
      2. Lọc theo trạng thái đang hoạt động:
         ```
         /shop/promotion?page=0&size=10&filter=status : 1
         ```
      3. Lọc promotion đang có hiệu lực và còn giới hạn sử dụng:
         ```
         /shop/promotion?page=0&size=10&filter=status : 1 and totalUsageLimit > 0 and endTime > '2025-01-01T00:00:00Z'
         ```
      4. Lọc nâng cao với sắp xếp:
         ```
         /shop/promotion?page=0&size=10&sort=startTime,desc&filter=status : 1 and startTime < '2025-12-31T23:59:59Z'
         ```
      ---
      **Cấu trúc dữ liệu trả về (example response) (Lưu ý không query theo gifts và rules):**
      ```json
      {
        "error": null,
        "content": {
          "info": {
            "page": 0,
            "size": 10,
            "pages": 0,
            "total": 0
          },
          "response": [
            {
              "id": "string",
              "name": "string",
              "status": 0,
              "startTime": "2025-01-01T00:00:00Z",
              "endTime": "2025-12-31T23:59:59Z",
              "totalUsageLimit": 0
            }
          ]
        }
      }
      ```
      ---
      """)
  @GetMapping
  public ResponseEntity<PaginationDTO.Response> getAllPromotionByShop(@Filter Specification<Promotion> spec,
      Pageable pageable) {
    var dto = PromotionService.fetchAllPromotionByShop(spec, pageable);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<RestResponse<PromotionResponseDto>> createPromotion(
      @Valid @RequestBody PromotionMapEntityCreateDto promotionMapEntityCreateDto) {

    var promotion = PromotionService.createPromotion(promotionMapEntityCreateDto);

    var promotionResponseDto = promotionMapper.toDto(promotion);
    return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.success(promotionResponseDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<RestResponse<PromotionResponseDto>> updatePromotion(@PathVariable("id") String id,
      @Valid @RequestBody PromotionMapEntityUpdateDto promotionMapEntityUpdateDto) {
    var promotion = PromotionService.updatePromotion(id, promotionMapEntityUpdateDto);
    var promotionResponseDto = promotionMapper.toDto(promotion);
    return ResponseEntity.ok(RestResponse.success(promotionResponseDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePromotion(@PathVariable("id") String id) {
    PromotionService.updatePromotionStatus(id, null, true);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> updatePromotionStatus(@PathVariable("id") String id,
      @Valid @RequestParam Integer status) {
    PromotionService.updatePromotionStatus(id, status, false);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/gifts")
  public ResponseEntity<Void> addPromotionGift(
      @PathVariable("id") String id,
      @Valid @RequestBody List<PromotionGiftCreateDto> promotionGiftCreateDto) {

    PromotionService.addPromotionGift(id, promotionGiftCreateDto);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/rules")
  public ResponseEntity<Void> addPromotionRule(
      @PathVariable("id") String id,
      @Valid @RequestBody List<PromotionRuleCreateDto> promotionRuleCreateDto) {
    PromotionService.addPromotionRule(id, promotionRuleCreateDto);
    return ResponseEntity.noContent().build();
  }

  @Operation(description = """
      String ở đây là danh sách các id của promotion gift.
      """)
  @DeleteMapping("/gifts")
  public ResponseEntity<Void> deletePromotionGift(@Valid @RequestBody List<String> promotionGiftIds) {
    PromotionService.deletePromotionGifts(promotionGiftIds);
    return ResponseEntity.noContent().build();
  }

  @Operation(description = """
      String ở đây là danh sách các id của promotion rule.
      """)
  @DeleteMapping("/rules")
  public ResponseEntity<Void> deletePromotionRule(@Valid @RequestBody List<String> promotionRuleIds) {
    PromotionService.deletePromotionRules(promotionRuleIds);
    return ResponseEntity.noContent().build();
  }

  @Operation(description = """
      Cập nhật status của promotion.
      Api này dành cho HangFire để chạy job mỗi ngày để cập nhật status của promotion.
      """)
  @PostMapping("/status/hang-fire")
  public ResponseEntity<Void> updateStatusPromotionByHangFire(
      @RequestHeader("API-KEY") String authorization,
      @RequestBody UpdateStatusForHangDto updateStatusForHangDto) {
    String expectedAuth = apiKey;
    if (!expectedAuth.equals(authorization)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    PromotionService.updateStatusPromotionByHangFire(updateStatusForHangDto);
    return ResponseEntity.noContent().build();
  }
}