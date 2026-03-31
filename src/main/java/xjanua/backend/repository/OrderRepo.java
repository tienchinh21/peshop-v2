package xjanua.backend.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import xjanua.backend.dto.voucher.shop.dash.OrderMetricsViewDto;
import xjanua.backend.model.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    @Query("""
                SELECT
                    o.createdAt AS createdAt,
                    o.originalPrice AS originalPrice,
                    o.shopVoucherDiscount AS shopVoucherDiscount,
                    o.user.id AS userId,
                    o.statusOrder AS statusOrder
                FROM Order o
                WHERE o.shop.id = :shopId
                  AND o.statusOrder IN :statusOrders
                  AND o.createdAt BETWEEN :startDate AND :endDate
            """)
    List<OrderMetricsViewDto> findMetricsByShopAndStatusAndCreatedAtBetween(
            String shopId,
            List<Integer> statusOrders,
            Instant startDate,
            Instant endDate);

    int countByShop_IdAndStatusOrderIn(String shopId, List<Integer> statuses);

    int countByShop_IdAndStatusOrderInAndCreatedAtBetween(String shopId, List<Integer> statuses, Instant start,
            Instant end);

    @EntityGraph(attributePaths = {
            "orderDetails",
            "orderDetails.product",
            "orderDetails.variant"
    })
    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    List<Order> findAllByIdInAndShop_Id(List<String> orderIds, String shopId);
}