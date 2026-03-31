package xjanua.backend.controller.shop;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.order.UpdateOrderStatusDto;
import xjanua.backend.model.Order;
import xjanua.backend.service.shop.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<RestResponse<PaginationDTO.Response>> getAllOrders(
            @Filter Specification<Order> spec,
            Pageable pageable) {
        var dto = orderService.fetchAllOrderByShop(spec, pageable);
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @Operation(description = """
                Cập nhật trạng thái các đơn hàng sang trạng thái xác nhận (status = 1).
            """)
    @PatchMapping("/status/confirmed")
    public ResponseEntity<RestResponse<Void>> updateOrderStatus(
            @RequestBody List<UpdateOrderStatusDto> updateOrderStatusDtos) {
        orderService.updateOrderStatusByShop(updateOrderStatusDtos, 1);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = """
                Cập nhật trạng thái các đơn hàng sang trạng thái hủy (status = 2).
            """)
    @PatchMapping("/status/rejected")
    public ResponseEntity<RestResponse<Void>> updateOrderStatusCancelled(
            @RequestBody List<UpdateOrderStatusDto> updateOrderStatusDtos) {
        orderService.updateOrderStatusByShop(updateOrderStatusDtos, 2);
        return ResponseEntity.noContent().build();
    }
}