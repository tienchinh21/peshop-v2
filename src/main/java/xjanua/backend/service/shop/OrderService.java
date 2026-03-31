package xjanua.backend.service.shop;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.order.OrderResponseSumaryDto;
import xjanua.backend.dto.order.UpdateOrderStatusDto;
import xjanua.backend.dto.order.detail.OrderDetailSumaryResponseDto;
import xjanua.backend.dto.voucher.shop.dash.OrderMetricsViewDto;
import xjanua.backend.mapper.OrderDetailMapper;
import xjanua.backend.mapper.OrderMapper;
import xjanua.backend.model.Order;
import xjanua.backend.model.OrderDetail;
import xjanua.backend.repository.OrderRepo;
import xjanua.backend.util.PaginationUtil;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderService {
        private final OrderRepo orderRepo;
        private final ShopService shopService;
        private final OrderMapper orderMapper;
        private final OrderDetailMapper orderDetailMapper;

        public Order fetchById(String orderId) {
                return orderRepo.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ResponseConstants.ORDER_NOT_FOUND_MESSAGE));
        }

        public List<OrderMetricsViewDto> fetchByShopIdAndStatusOrderAndCreatedAtBetween(Instant startDate,
                        Instant endDate,
                        List<Integer> statusOrders, String shopId, Boolean useCache) {
                List<OrderMetricsViewDto> orders = orderRepo.findMetricsByShopAndStatusAndCreatedAtBetween(shopId,
                                statusOrders,
                                startDate,
                                endDate);
                return orders;
        }

        public int countByShopIdAndStatusOrder(List<Integer> statusOrders, String shopId) {
                return orderRepo.countByShop_IdAndStatusOrderIn(shopId, statusOrders);
        }

        public int countByShopIdAndStatusOrderAndCreatedAtBetween(List<Integer> statusOrders, String shopId,
                        Instant start, Instant end) {
                return orderRepo.countByShop_IdAndStatusOrderInAndCreatedAtBetween(shopId, statusOrders, start, end);
        }

        public PaginationDTO.Response fetchAllOrderByShop(Specification<Order> specification, Pageable pageable) {
                String shopId = shopService.fetchByUserLogin().getId();

                Specification<Order> shopSpec = (root, query, cb) -> cb.and(
                                cb.equal(root.get("shop").get("id"), shopId));

                Specification<Order> finalSpec = (specification == null)
                                ? shopSpec
                                : specification.and(shopSpec);

                Page<Order> orders = orderRepo.findAll(finalSpec, pageable);

                PaginationDTO.Response response = new PaginationDTO.Response();
                PaginationDTO.Info info = PaginationUtil.buildInfo(orders, pageable);

                List<OrderResponseSumaryDto> orderDTOs = orders.getContent()
                                .stream()
                                .map(order -> {

                                        OrderResponseSumaryDto dto = orderMapper.toOrderResponseSumaryDto(order);

                                        // Tính doanh thu = originalPrice - shopVoucherDiscount
                                        BigDecimal originalPrice = order.getOriginalPrice() != null
                                                        ? order.getOriginalPrice()
                                                        : BigDecimal.ZERO;
                                        BigDecimal shopVoucherDiscount = order.getShopVoucherDiscount() != null
                                                        ? order.getShopVoucherDiscount()
                                                        : BigDecimal.ZERO;
                                        BigDecimal revenue = originalPrice.subtract(shopVoucherDiscount);
                                        dto.setRevenue(revenue);

                                        List<OrderDetailSumaryResponseDto> detailDTOs = order.getOrderDetails() != null
                                                        ? order.getOrderDetails()
                                                                        .stream()
                                                                        .map(detail -> {
                                                                                List<String> pvNames = getPropertyValueNames(
                                                                                                detail);
                                                                                return orderDetailMapper
                                                                                                .toOrderDetailSumaryResponseDto(
                                                                                                                detail,
                                                                                                                pvNames);
                                                                        })
                                                                        .toList()
                                                        : List.of();

                                        dto.setOrderDetails(detailDTOs);

                                        return dto;
                                })
                                .toList();

                response.setInfo(info);
                response.setResponse(orderDTOs);
                return response;
        }

        /**
         * Get property value names from OrderDetail
         * Through variant -> variantValues -> propertyValue -> value
         */
        private List<String> getPropertyValueNames(OrderDetail detail) {
                if (detail == null || detail.getVariant() == null
                                || detail.getVariant().getVariantValues() == null) {
                        return List.of();
                }

                return detail.getVariant().getVariantValues()
                                .stream()
                                .filter(vv -> vv != null && vv.getPropertyValue() != null)
                                .map(vv -> vv.getPropertyValue().getValue())
                                .filter(value -> value != null && !value.isEmpty())
                                .toList();
        }

        @Transactional
        public void updateOrderStatusByShop(List<UpdateOrderStatusDto> updateOrderStatusDtos, Integer status) {
                String shopId = shopService.fetchByUserLogin().getId();

                List<String> orderIds = updateOrderStatusDtos.stream()
                                .map(UpdateOrderStatusDto::getOrderId)
                                .toList();
                List<Order> orders = orderRepo.findAllByIdInAndShop_Id(orderIds, shopId);

                if (orders.size() != orderIds.size()) {
                        throw new ResourceNotFoundException(
                                        ResponseConstants.ORDER_NOT_FOUND_MESSAGE);
                }

                orders.forEach(order -> {
                        if (order.getStatusOrder() != 0) {
                                throw new BadRequestException(
                                                "Order " + order.getId() + " status is not pending");
                        }
                        order.setStatusOrder(status);
                });

                orderRepo.saveAll(orders);
        }
}