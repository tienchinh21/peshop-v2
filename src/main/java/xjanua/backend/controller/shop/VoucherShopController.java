package xjanua.backend.controller.shop;

import java.time.LocalDate;
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
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.product.UpdateStatusForHangDto;
import xjanua.backend.dto.voucher.shop.VoucherShopCreateDto;
import xjanua.backend.dto.voucher.shop.VoucherShopDetailDto;
import xjanua.backend.dto.voucher.shop.VoucherShopUpdateDto;
import xjanua.backend.dto.voucher.shop.VoucherSummaryDto;
import xjanua.backend.dto.voucher.shop.dash.CampaignMetricsDto;
import xjanua.backend.mapper.VoucherShopMapper;
import xjanua.backend.model.VoucherShop;
import xjanua.backend.service.shop.OrderVoucherService;
import xjanua.backend.service.shop.VoucherShopDashService;
import xjanua.backend.service.shop.VoucherShopService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/voucher-shop")
public class VoucherShopController {

  @Value("${api.key}")
  private String apiKey;

  private final VoucherShopService voucherShopService;
  private final VoucherShopMapper voucherShopMapper;
  private final OrderVoucherService orderVoucherService;
  private final VoucherShopDashService voucherShopDashService;

  @PreAuthorize("hasAuthority('Shop')")
  @GetMapping("/{id}")
  public ResponseEntity<RestResponse<VoucherShopDetailDto>> getVoucherShopById(@PathVariable("id") String id) {
    var dto = voucherShopMapper.toVoucherShopDetailDto(voucherShopService.fetchById(id));
    return ResponseEntity.ok(RestResponse.success(dto));
  }

  @Operation(description = """
      Trả về các Order có sử dụng voucher **{id}** của Shop.
      Chỉ phân trang không filter.
      """)
  @PreAuthorize("hasAuthority('Shop')")
  @GetMapping("/{id}/orders")
  public ResponseEntity<PaginationDTO.Response> getOrdersByVoucherShopId(
      @PathVariable("id") String id,
      Pageable pageable) {

    var dto = orderVoucherService.fetchAllByVoucherShopId(id, pageable);

    return ResponseEntity.ok(dto);
  }

  @Operation(description = """
      Trả về danh sách voucher của shop có thể được lọc, phân trang và sắp xếp.

      **Tham số:**
      - **page**: Số trang (bắt đầu từ 0)
      - **size**: Số phần tử mỗi trang
      - **sort**: Dạng `property,(asc|desc)` — ví dụ: `startTime,desc`
      - **filter**: Biểu thức lọc theo cú pháp Spring Filter.

      **Cách sử dụng filter (Spring Filter):**
      - **Toán tử so sánh:** `:`, `>`, `>=`, `<`, `<=`
        > Ví dụ: `type : 1`, `discountValue >= 10000`
      - **Toán tử logic:** `and`, `or`, `not`
        > Ví dụ: `status : 1 and quantity > 0`
      - **So khớp chuỗi:** `~` (like), `~~` (like không phân biệt hoa thường)
        > Ví dụ: `code ~~ 'SALE'`
        > Nếu muốn so khớp **chính xác toàn bộ chuỗi**, hãy dùng `:` thay vì `~`

      **Ví dụ sử dụng:**
      1. Lọc theo mã voucher:
         ```
         /voucher-shop?page=0&size=10&filter=code ~~ 'SALE'
         ```
      2. Lọc theo trạng thái đang hoạt động:
         ```
         /voucher-shop?page=0&size=10&filter=status : 1
         ```
      3. Lọc voucher đang có hiệu lực và còn số lượng:
         ```
         /voucher-shop?page=0&size=10&filter=status : 1 and quantity > 0 and endTime > '2025-01-01T00:00:00Z'
         ```
      4. Lọc nâng cao với sắp xếp:
         ```
         /voucher-shop?page=0&size=10&sort=startTime,desc&filter=type : 1 and discountValue >= 50000 and minimumOrderValue <= 200000
         ```
      ---
      **Cấu trúc dữ liệu trả về (example response):**
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
              "code": "string",
              "name": "string",
              "type": 0,
              "discountValue": 0.000,
              "minimumOrderValue": 0.000,
              "quantity": 0,
              "quantityUsed": 0,
              "startTime": "2025-01-01T00:00:00Z",
              "endTime": "2025-12-31T23:59:59Z",
              "status": 0,
            }
          ]
        }
      }
      ```
      ---
      """)
  @PreAuthorize("hasAuthority('Shop')")
  @GetMapping
  public ResponseEntity<PaginationDTO.Response> getAllVoucherShop(@Filter Specification<VoucherShop> spec,
      Pageable pageable) {
    var dto = voucherShopService.fetchAllVoucherShopByShop(spec, pageable);
    return ResponseEntity.ok(dto);
  }

  @Operation(description = """
      Trả về thống kê tổng quan về chiến dịch voucher shop trong khoảng thời gian chỉ định.

      **Tham số:**
      - **startDate**: Ngày bắt đầu (LocalDate, định dạng: yyyy-MM-dd)
      - **endDate**: Ngày kết thúc (LocalDate, định dạng: yyyy-MM-dd)
      - **period**: Loại khoảng thời gian - `today_or_yesterday`, `past7days`, hoặc `past30days`

      **Dữ liệu trả về:**
      - **sales**: Doanh thu (giá gốc - giảm giá voucher) với so sánh kỳ trước (value, oldValue, increment, change_rate, points)
      - **orders**: Số lượng đơn hàng với so sánh kỳ trước (value, oldValue, increment, change_rate, points)
      - **usageRate**: Tỷ lệ sử dụng voucher (% đơn hàng có sử dụng voucher) với so sánh kỳ trước
      - **buyers**: Số lượng người mua duy nhất với so sánh kỳ trước (value, oldValue, increment, change_rate, points)
      - **points**: Dữ liệu time-series theo giờ (nếu period = today_or_yesterday) hoặc theo ngày (các period khác)

      **Ví dụ sử dụng:**
      ```
      /dashboard/voucherShop?startDate=2024-01-01&endDate=2024-01-07&period=past7days
      ```
      """)
  @PreAuthorize("hasAuthority('Shop')")
  @GetMapping("/dashboard")
  public ResponseEntity<RestResponse<CampaignMetricsDto>> getVoucherShopDashboardSummary(
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate,
      @RequestParam String period) {
    var response = voucherShopDashService.getVoucherShopDashboardSummary(startDate, endDate, period);
    return ResponseEntity.ok(RestResponse.success(response));
  }

  @Operation(description = """
      Tạo voucher shop mới.

      **Lưu ý:**
      - **Code** phải unique trong shop (không trùng với voucher khác của shop, trừ voucher đã ENDED)
      - **Code** tối đa 55 ký tự
      - **startTime** phải là thời gian tương lai và **endTime** > **startTime**
      - **type**: 0 = FIXED_AMOUNT, 1 = PERCENTAGE
      - Nếu **type = 1** (PERCENTAGE), **discountValue** không được > 100 và **maxDiscountAmount** có thể có giá trị hoặc **không giới hạn**
      - Nếu **type = 0** (FIXED_AMOUNT), **maxDiscountAmount** sẽ bị bỏ qua
      - Voucher được tạo với **status = INACTIVE** (0)
      """)
  @PreAuthorize("hasAuthority('Shop')")
  @PostMapping
  public ResponseEntity<RestResponse<VoucherSummaryDto>> createVoucherShop(
      @RequestBody VoucherShopCreateDto voucherShopCreateDto) {
    var dto = voucherShopMapper
        .toVoucherSummaryDto(voucherShopService.createVoucherShop(voucherShopCreateDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.success(dto));
  }

  @Operation(description = """
      Cập nhật voucher shop.

      **Lưu ý:**
      - Không thể update voucher có **status = ENDED** (2)
      - Nếu **status = INACTIVE** (0): có thể update tất cả (name, discountValue, minimumOrderValue, quantity, startTime, endTime) — **endTime** phải > **startTime**
      - Nếu **status = ACTIVE** (1): chỉ có thể update **name** và **quantity**
      - **Code** không thể thay đổi trong mọi trường hợp
      - Voucher phải thuộc về shop của user đang login
      """)
  @PreAuthorize("hasAuthority('Shop')")
  @PutMapping("/{id}")
  public ResponseEntity<RestResponse<VoucherSummaryDto>> updateVoucherShop(@PathVariable("id") String id,
      @RequestBody VoucherShopUpdateDto voucherShopUpdateDto) {
    var dto = voucherShopMapper
        .toVoucherSummaryDto(voucherShopService.updateVoucherShop(id, voucherShopUpdateDto));
    return ResponseEntity.ok(RestResponse.success(dto));
  }

  @Operation(description = """
      Kết thúc voucher shop.
      """)
  @PreAuthorize("hasAuthority('Shop')")
  @PatchMapping("/end")
  public ResponseEntity<Void> endVoucherShop(@RequestBody List<String> voucherShopIds) {
    voucherShopService.endVoucherShop(voucherShopIds);
    return ResponseEntity.noContent().build();
  }

  @Operation(description = """
      Chỉ xoá được các voucher shop có **status = INACTIVE** (0)
      """)
  @PreAuthorize("hasAuthority('Shop')")
  @DeleteMapping
  public ResponseEntity<Void> deleteVoucherShops(@RequestBody List<String> voucherShopIds) {
    voucherShopService.deleteVoucherShops(voucherShopIds);
    return ResponseEntity.noContent().build();
  }

  @Operation(description = """
      Cập nhật status của voucher shop.
      Api này dành cho HangFire để chạy job mỗi ngày để cập nhật status của voucher shop.
      """)
  @PostMapping("/status/hang-fire")
  public ResponseEntity<Void> updateStatusVoucherShopByHangFire(
      @RequestHeader("API-KEY") String authorization,
      @RequestBody UpdateStatusForHangDto updateStatusForHangDto) {
    String expectedAuth = apiKey;
    if (!expectedAuth.equals(authorization)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    voucherShopService.updateStatusVoucherShopByHangFire(updateStatusForHangDto);
    return ResponseEntity.noContent().build();
  }
}