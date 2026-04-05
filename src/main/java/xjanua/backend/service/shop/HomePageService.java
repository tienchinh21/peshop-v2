package xjanua.backend.service.shop;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PointsDto;
import xjanua.backend.dto.homepage.dashboardDto;
import xjanua.backend.dto.homepage.todolistShopDto;
import xjanua.backend.dto.homepage.MetricDTO.CancellationSalesMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.OrderConversionRateMetricDto;
import xjanua.backend.dto.homepage.MetricDTO.OrdersCancelledMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.OrdersMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.ProductClicksMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.RevenuePerOrderMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.SalesMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.VisitsMetricsDto;
import xjanua.backend.dto.voucher.shop.dash.OrderMetricsViewDto;
import xjanua.backend.model.UserViewProduct;
import xjanua.backend.model.UserViewShop;
import xjanua.backend.service.RedisService;

@Service
@RequiredArgsConstructor
public class HomePageService {
    private final OrderService orderService;
    private final ShopService shopService;
    private final ProductService productService;
    private final UserViewShopService userViewShopService;
    private final UserViewProductService userViewProductService;
    private final RedisService redisService;

    public todolistShopDto getTodolistShop() {
        String shopId = shopService.fetchByUserLogin().getId();

        int waitingForDelivery = orderService.countByShopIdAndStatusOrder(List.of(0), shopId);
        int processed = orderService.countByShopIdAndStatusOrder(List.of(1), shopId);
        int returnsAndCancellations = orderService.countByShopIdAndStatusOrder(List.of(2, 6), shopId);
        int productLocked = productService.getProductLocked(shopId);

        return todolistShopDto.builder()
                .waitingForDelivery(waitingForDelivery)
                .processed(processed)
                .returnsAndCancellations(returnsAndCancellations)
                .productlocked(productLocked)
                .build();
    }

    public dashboardDto getDashboard(LocalDate startDate, LocalDate endDate,
            String period) {
        String shopId = shopService.fetchByUserLogin().getId();

        validateRange(startDate, endDate, period);

        String cacheKey = "peshop:dashboard:" + shopId + ":" + startDate + ":" + endDate + ":" + period;
        try {
            dashboardDto cachedDashboard = redisService.getObject(cacheKey, dashboardDto.class);
            if (cachedDashboard != null) {
                return cachedDashboard;
            }
        } catch (Exception e) {
        }

        Instant startInstant = toStartOfDayUtc(startDate);
        Instant endInstant = toEndOfDayUtc(endDate);

        List<OrderMetricsViewDto> currentOrders = orderService.fetchByShopIdAndStatusOrderAndCreatedAtBetween(
                startInstant, endInstant, List.of(1, 2, 3, 4, 5, 6), shopId, true);

        PastDateRange pastRange = calculatePastDateRange(startDate, endDate, period);
        List<OrderMetricsViewDto> pastOrders = orderService.fetchByShopIdAndStatusOrderAndCreatedAtBetween(
                pastRange.pastStart(), pastRange.pastEnd(), List.of(1, 3, 4, 5), shopId, false);

        List<UserViewShop> currentVisits = userViewShopService.getUserViewShopsDistinctByUserId(
                shopId, startInstant, endInstant);
        List<UserViewShop> pastVisits = userViewShopService.getUserViewShopsDistinctByUserId(
                shopId, pastRange.pastStart(), pastRange.pastEnd());

        // Lấy product clicks
        List<UserViewProduct> currentProductClicks = userViewProductService.getAllViewsByShopAndCreatedAtBetween(
                shopId, startInstant, endInstant);
        List<UserViewProduct> pastProductClicks = userViewProductService.getAllViewsByShopAndCreatedAtBetween(
                shopId, pastRange.pastStart(), pastRange.pastEnd());

        dashboardDto dto = new dashboardDto();

        dto.setSales(createSalesMetrics(currentOrders, pastOrders, startDate, endDate, period));
        dto.setOrders(createOrdersMetrics(currentOrders, pastOrders, startDate, endDate, period));
        dto.setOrdersCancelled(createOrdersCancelledMetrics(currentOrders, pastOrders, startDate, endDate, period));
        dto.setRevenuePerOrder(createRevenuePerOrderMetrics(currentOrders, pastOrders, startDate, endDate, period));
        dto.setCancellationSales(createCancellationSalesMetrics(currentOrders, pastOrders, startDate, endDate, period));
        dto.setVisits(createVisitsMetrics(currentVisits, pastVisits, startDate, endDate, period));
        dto.setOrderConversionRate(createOrderConversionRateMetrics(
                currentOrders, pastOrders, currentVisits, pastVisits, startDate, endDate, period));
        dto.setProductClicks(createProductClicksMetrics(
                currentProductClicks, pastProductClicks, startDate, endDate, period));

        try {
            redisService.setObject(cacheKey, dto, 180);
        } catch (Exception e) {
        }

        return dto;
    }

    private SalesMetricsDto createSalesMetrics(
            List<OrderMetricsViewDto> currentOrders,
            List<OrderMetricsViewDto> pastOrders,
            LocalDate startDate, LocalDate endDate, String period) {

        // Tổng giá trị gốc của các đơn hiện tại
        BigDecimal value = currentOrders.stream()
                .map(OrderMetricsViewDto::getOriginalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tổng giá trị gốc của các đơn quá khứ
        BigDecimal oldValue = pastOrders.stream()
                .map(OrderMetricsViewDto::getOriginalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Chênh lệch
        BigDecimal increment = value.subtract(oldValue);

        // Tỷ lệ thay đổi
        float change_rate = oldValue.compareTo(BigDecimal.ZERO) == 0
                ? (value.compareTo(BigDecimal.ZERO) > 0 ? 100f : 0f)
                : increment.divide(oldValue, 2, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).floatValue();

        // Tạo các điểm cho biểu đồ dựa trên giá trị gốc
        List<PointsDto> points = createPointsBase(startDate, endDate, period);
        fillPoints(currentOrders, points, startDate, endDate, period,
                OrderMetricsViewDto::getOriginalPrice);

        // Build DTO trả về
        SalesMetricsDto dto = new SalesMetricsDto();
        dto.setValue(value);
        dto.setOldValue(oldValue);
        dto.setIncrement(increment);
        dto.setChangeRate(change_rate);
        dto.setPoints(points);

        return dto;
    }

    private OrdersMetricsDto createOrdersMetrics(List<OrderMetricsViewDto> currentOrders,
            List<OrderMetricsViewDto> pastOrders, LocalDate startDate, LocalDate endDate, String period) {

        // Tổng số đơn hiện tại
        int value = currentOrders.size();

        // Tổng số đơn quá khứ
        int oldValue = pastOrders.size();

        // Chênh lệch
        int increment = value - oldValue;

        // Tỷ lệ thay đổi
        float changeRate = oldValue == 0
                ? (value > 0 ? 100f : 0f)
                : (float) increment / oldValue * 100f;

        // Tạo các điểm cho biểu đồ dựa trên số lượng đơn
        List<PointsDto> points = createPointsBase(startDate, endDate, period);
        fillPoints(currentOrders, points, startDate, endDate, period,
                order -> BigDecimal.ONE); // Mỗi đơn đếm là 1

        // Build DTO trả về
        OrdersMetricsDto dto = new OrdersMetricsDto();
        dto.setValue(value);
        dto.setOldValue(oldValue);
        dto.setIncrement(increment);
        dto.setChangeRate(changeRate);
        dto.setPoints(points);

        return dto;
    }

    private OrdersCancelledMetricsDto createOrdersCancelledMetrics(List<OrderMetricsViewDto> currentOrders,
            List<OrderMetricsViewDto> pastOrders, LocalDate startDate, LocalDate endDate, String period) {

        // Lọc các đơn hủy (status = 2 hoặc 6) từ currentOrders
        List<OrderMetricsViewDto> currentCancelledOrders = currentOrders.stream()
                .filter(order -> order.getStatusOrder() == 2 || order.getStatusOrder() == 6)
                .toList();

        // Lọc các đơn hủy (status = 2 hoặc 6) từ pastOrders
        List<OrderMetricsViewDto> pastCancelledOrders = pastOrders.stream()
                .filter(order -> order.getStatusOrder() == 2 || order.getStatusOrder() == 6)
                .toList();

        // Tổng số đơn hủy hiện tại
        int value = currentCancelledOrders.size();

        // Tổng số đơn hủy quá khứ
        int oldValue = pastCancelledOrders.size();

        // Chênh lệch
        int increment = value - oldValue;

        // Tỷ lệ thay đổi
        float changeRate = oldValue == 0
                ? (value > 0 ? 100f : 0f)
                : (float) increment / oldValue * 100f;

        // Tạo các điểm cho biểu đồ dựa trên số lượng đơn hủy
        List<PointsDto> points = createPointsBase(startDate, endDate, period);
        fillPoints(currentCancelledOrders, points, startDate, endDate, period,
                order -> BigDecimal.ONE); // Mỗi đơn đếm là 1

        // Build DTO trả về
        OrdersCancelledMetricsDto dto = new OrdersCancelledMetricsDto();
        dto.setValue(value);
        dto.setOldValue(oldValue);
        dto.setIncrement(increment);
        dto.setChangeRate(changeRate);
        dto.setPoints(points);

        return dto;
    }

    private RevenuePerOrderMetricsDto createRevenuePerOrderMetrics(
            List<OrderMetricsViewDto> currentOrders,
            List<OrderMetricsViewDto> pastOrders,
            LocalDate startDate, LocalDate endDate, String period) {

        // Tổng giá trị gốc của các đơn hiện tại
        BigDecimal totalSales = currentOrders.stream()
                .map(OrderMetricsViewDto::getOriginalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Số đơn hiện tại
        int orderCount = currentOrders.size();

        // Doanh thu trung bình mỗi đơn hiện tại
        BigDecimal value = orderCount > 0
                ? totalSales.divide(BigDecimal.valueOf(orderCount), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Tổng giá trị gốc của các đơn quá khứ
        BigDecimal pastTotalSales = pastOrders.stream()
                .map(OrderMetricsViewDto::getOriginalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Số đơn quá khứ
        int pastOrderCount = pastOrders.size();

        // Doanh thu trung bình mỗi đơn quá khứ
        BigDecimal oldValue = pastOrderCount > 0
                ? pastTotalSales.divide(BigDecimal.valueOf(pastOrderCount), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Chênh lệch
        BigDecimal increment = value.subtract(oldValue);

        // Tỷ lệ thay đổi
        float changeRate = oldValue.compareTo(BigDecimal.ZERO) == 0
                ? (value.compareTo(BigDecimal.ZERO) > 0 ? 100f : 0f)
                : increment.divide(oldValue, 2, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).floatValue();

        // Tạo các điểm cho biểu đồ
        List<PointsDto> points = createPointsBase(startDate, endDate, period);
        fillPointsRevenuePerOrder(currentOrders, points, startDate, endDate, period);

        // Build DTO trả về
        RevenuePerOrderMetricsDto dto = new RevenuePerOrderMetricsDto();
        dto.setValue(value);
        dto.setOldValue(oldValue);
        dto.setIncrement(increment);
        dto.setChangeRate(changeRate);
        dto.setPoints(points);

        return dto;
    }

    private CancellationSalesMetricsDto createCancellationSalesMetrics(
            List<OrderMetricsViewDto> currentOrders,
            List<OrderMetricsViewDto> pastOrders,
            LocalDate startDate, LocalDate endDate, String period) {

        // Lọc các đơn hủy (status = 2 hoặc 6) từ currentOrders
        List<OrderMetricsViewDto> currentCancelledOrders = currentOrders.stream()
                .filter(order -> order.getStatusOrder() == 2 || order.getStatusOrder() == 6)
                .toList();

        // Lọc các đơn hủy (status = 2 hoặc 6) từ pastOrders
        List<OrderMetricsViewDto> pastCancelledOrders = pastOrders.stream()
                .filter(order -> order.getStatusOrder() == 2 || order.getStatusOrder() == 6)
                .toList();

        // Tổng giá trị gốc của các đơn hủy hiện tại
        BigDecimal value = currentCancelledOrders.stream()
                .map(OrderMetricsViewDto::getOriginalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tổng giá trị gốc của các đơn hủy quá khứ
        BigDecimal oldValue = pastCancelledOrders.stream()
                .map(OrderMetricsViewDto::getOriginalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Chênh lệch
        BigDecimal increment = value.subtract(oldValue);

        // Tỷ lệ thay đổi
        float changeRate = oldValue.compareTo(BigDecimal.ZERO) == 0
                ? (value.compareTo(BigDecimal.ZERO) > 0 ? 100f : 0f)
                : increment.divide(oldValue, 2, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).floatValue();

        // Tạo các điểm cho biểu đồ dựa trên giá trị gốc của các đơn hủy
        List<PointsDto> points = createPointsBase(startDate, endDate, period);
        fillPoints(currentCancelledOrders, points, startDate, endDate, period,
                OrderMetricsViewDto::getOriginalPrice);

        // Build DTO trả về
        CancellationSalesMetricsDto dto = new CancellationSalesMetricsDto();
        dto.setValue(value);
        dto.setOldValue(oldValue);
        dto.setIncrement(increment);
        dto.setChangeRate(changeRate);
        dto.setPoints(points);

        return dto;
    }

    private VisitsMetricsDto createVisitsMetrics(
            List<UserViewShop> currentVisits,
            List<UserViewShop> pastVisits,
            LocalDate startDate, LocalDate endDate, String period) {

        // Tổng số lượt visit hiện tại (đã lọc trùng userId)
        int value = currentVisits.size();

        // Tổng số lượt visit quá khứ (đã lọc trùng userId)
        int oldValue = pastVisits.size();

        // Chênh lệch
        int increment = value - oldValue;

        // Tỷ lệ thay đổi
        float changeRate = oldValue == 0
                ? (value > 0 ? 100f : 0f)
                : (float) increment / oldValue * 100f;

        // Tạo các điểm cho biểu đồ dựa trên số lượng visit
        List<PointsDto> points = createPointsBase(startDate, endDate, period);
        fillPointsVisits(currentVisits, points, startDate, endDate, period);

        // Build DTO trả về
        VisitsMetricsDto dto = new VisitsMetricsDto();
        dto.setValue(value);
        dto.setOldValue(oldValue);
        dto.setIncrement(increment);
        dto.setChangeRate(changeRate);
        dto.setPoints(points);

        return dto;
    }

    private OrderConversionRateMetricDto createOrderConversionRateMetrics(
            List<OrderMetricsViewDto> currentOrders,
            List<OrderMetricsViewDto> pastOrders,
            List<UserViewShop> currentVisits,
            List<UserViewShop> pastVisits,
            LocalDate startDate, LocalDate endDate, String period) {

        // Số đơn hiện tại
        int currentOrderCount = currentOrders.size();

        // Số lượt visit hiện tại (đã lọc trùng userId)
        int currentVisitCount = currentVisits.size();

        // Tỷ lệ chuyển đổi hiện tại (%)
        float value = currentVisitCount > 0
                ? (float) currentOrderCount / currentVisitCount * 100f
                : 0f;

        // Số đơn quá khứ
        int pastOrderCount = pastOrders.size();

        // Số lượt visit quá khứ (đã lọc trùng userId)
        int pastVisitCount = pastVisits.size();

        // Tỷ lệ chuyển đổi quá khứ (%)
        float oldValue = pastVisitCount > 0
                ? (float) pastOrderCount / pastVisitCount * 100f
                : 0f;

        // Chênh lệch
        float increment = value - oldValue;

        // Tỷ lệ thay đổi
        float changeRate = oldValue == 0
                ? (value > 0 ? 100f : 0f)
                : increment / oldValue * 100f;

        // Tạo các điểm cho biểu đồ
        List<PointsDto> points = createPointsBase(startDate, endDate, period);
        fillPointsOrderConversionRate(currentOrders, currentVisits, points, startDate, endDate, period);

        // Build DTO trả về
        OrderConversionRateMetricDto dto = new OrderConversionRateMetricDto();
        dto.setValue(value);
        dto.setOldValue(oldValue);
        dto.setIncrement(increment);
        dto.setChangeRate(changeRate);
        dto.setPoints(points);

        return dto;
    }

    private ProductClicksMetricsDto createProductClicksMetrics(
            List<UserViewProduct> currentProductClicks,
            List<UserViewProduct> pastProductClicks,
            LocalDate startDate, LocalDate endDate, String period) {

        // Tổng số lượt click sản phẩm hiện tại
        int value = currentProductClicks.size();

        // Tổng số lượt click sản phẩm quá khứ
        int oldValue = pastProductClicks.size();

        // Chênh lệch
        int increment = value - oldValue;

        // Tỷ lệ thay đổi
        float changeRate = oldValue == 0
                ? (value > 0 ? 100f : 0f)
                : (float) increment / oldValue * 100f;

        // Tạo các điểm cho biểu đồ dựa trên số lượng click
        List<PointsDto> points = createPointsBase(startDate, endDate, period);
        fillPointsProductClicks(currentProductClicks, points, startDate, endDate, period);

        // Build DTO trả về
        ProductClicksMetricsDto dto = new ProductClicksMetricsDto();
        dto.setValue(value);
        dto.setOldValue(oldValue);
        dto.setIncrement(increment);
        dto.setChangeRate(changeRate);
        dto.setPoints(points);

        return dto;
    }

    private List<PointsDto> createPointsBase(LocalDate startDate, LocalDate endDate, String period) {

        List<PointsDto> points = new ArrayList<>();

        if ("today_or_yesterday".equals(period)) {
            for (int hour = 0; hour < 24; hour++) {
                PointsDto p = new PointsDto();
                p.setTime(startDate.atTime(hour, 0).atOffset(ZoneOffset.UTC).toLocalDateTime());
                p.setValue("0");
                points.add(p);
            }
        } else {
            LocalDate d = startDate;
            while (!d.isAfter(endDate)) {
                PointsDto p = new PointsDto();
                p.setTime(d.atStartOfDay().atOffset(ZoneOffset.UTC).toLocalDateTime());
                p.setValue("0");
                points.add(p);
                d = d.plusDays(1);
            }
        }

        return points;
    }

    private void fillPoints(
            List<OrderMetricsViewDto> orders,
            List<PointsDto> points,
            LocalDate startDate,
            LocalDate endDate,
            String period,
            java.util.function.Function<OrderMetricsViewDto, BigDecimal> valueMapper) {

        for (OrderMetricsViewDto order : orders) {
            LocalDateTime createdAt = LocalDateTime.ofInstant(order.getCreatedAt(), ZoneOffset.UTC);
            int index = resolvePointIndex(createdAt, startDate, endDate, period);

            if (index >= 0 && index < points.size()) {
                PointsDto p = points.get(index);
                String current = p.getValue() == null ? "0" : p.getValue();
                BigDecimal currentVal = new BigDecimal(current);
                BigDecimal nextVal = currentVal.add(valueMapper.apply(order));
                p.setValue(nextVal.toString());
            }
        }
    }

    private int resolvePointIndex(LocalDateTime createdAt, LocalDate startDate, LocalDate endDate, String period) {

        if ("today_or_yesterday".equals(period)) {
            return createdAt.getHour();
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, createdAt.toLocalDate());
        if (daysBetween < 0)
            return -1; // trước range
        if (createdAt.toLocalDate().isAfter(endDate))
            return -1; // sau range

        return (int) daysBetween;
    }

    private void fillPointsVisits(
            List<UserViewShop> visits,
            List<PointsDto> points,
            LocalDate startDate,
            LocalDate endDate,
            String period) {

        for (UserViewShop visit : visits) {
            LocalDateTime createdAt = LocalDateTime.ofInstant(visit.getCreatedAt(), ZoneOffset.UTC);
            int index = resolvePointIndex(createdAt, startDate, endDate, period);

            if (index >= 0 && index < points.size()) {
                PointsDto p = points.get(index);
                String current = p.getValue() == null ? "0" : p.getValue();
                BigDecimal currentVal = new BigDecimal(current);
                BigDecimal nextVal = currentVal.add(BigDecimal.ONE); // Mỗi visit đếm là 1
                p.setValue(nextVal.toString());
            }
        }
    }

    private void fillPointsProductClicks(
            List<UserViewProduct> productClicks,
            List<PointsDto> points,
            LocalDate startDate,
            LocalDate endDate,
            String period) {

        for (UserViewProduct click : productClicks) {
            LocalDateTime createdAt = LocalDateTime.ofInstant(click.getCreatedAt(), ZoneOffset.UTC);
            int index = resolvePointIndex(createdAt, startDate, endDate, period);

            if (index >= 0 && index < points.size()) {
                PointsDto p = points.get(index);
                String current = p.getValue() == null ? "0" : p.getValue();
                BigDecimal currentVal = new BigDecimal(current);
                BigDecimal nextVal = currentVal.add(BigDecimal.ONE); // Mỗi click đếm là 1
                p.setValue(nextVal.toString());
            }
        }
    }

    private void fillPointsOrderConversionRate(
            List<OrderMetricsViewDto> orders,
            List<UserViewShop> visits,
            List<PointsDto> points,
            LocalDate startDate,
            LocalDate endDate,
            String period) {

        // Nhóm orders và visits theo index để tính conversion rate cho từng điểm
        for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
            PointsDto point = points.get(pointIndex);
            int orderCount = 0;
            int visitCount = 0;

            // Đếm số đơn trong khoảng thời gian này
            for (OrderMetricsViewDto order : orders) {
                LocalDateTime createdAt = LocalDateTime.ofInstant(order.getCreatedAt(), ZoneOffset.UTC);
                int index = resolvePointIndex(createdAt, startDate, endDate, period);
                if (index == pointIndex) {
                    orderCount++;
                }
            }

            // Đếm số lượt visit trong khoảng thời gian này
            for (UserViewShop visit : visits) {
                LocalDateTime createdAt = LocalDateTime.ofInstant(visit.getCreatedAt(), ZoneOffset.UTC);
                int index = resolvePointIndex(createdAt, startDate, endDate, period);
                if (index == pointIndex) {
                    visitCount++;
                }
            }

            // Tính conversion rate (%)
            float conversionRate = visitCount > 0
                    ? (float) orderCount / visitCount * 100f
                    : 0f;

            point.setValue(String.valueOf(conversionRate));
        }
    }

    private void fillPointsRevenuePerOrder(
            List<OrderMetricsViewDto> orders,
            List<PointsDto> points,
            LocalDate startDate,
            LocalDate endDate,
            String period) {

        // Nhóm orders theo index để tính revenue per order cho từng điểm
        for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
            PointsDto point = points.get(pointIndex);
            List<OrderMetricsViewDto> ordersInPeriod = new ArrayList<>();

            for (OrderMetricsViewDto order : orders) {
                LocalDateTime createdAt = LocalDateTime.ofInstant(order.getCreatedAt(), ZoneOffset.UTC);
                int index = resolvePointIndex(createdAt, startDate, endDate, period);

                if (index == pointIndex) {
                    ordersInPeriod.add(order);
                }
            }

            // Tính revenue per order cho điểm này
            if (!ordersInPeriod.isEmpty()) {
                BigDecimal totalSales = ordersInPeriod.stream()
                        .map(OrderMetricsViewDto::getOriginalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal revenuePerOrder = totalSales.divide(
                        BigDecimal.valueOf(ordersInPeriod.size()),
                        2,
                        java.math.RoundingMode.HALF_UP);
                point.setValue(revenuePerOrder.toString());
            }
        }
    }

    private void validateRange(LocalDate startDate, LocalDate endDate, String rangeType) {

        switch (rangeType) {
            case "today_or_yesterday" -> {
                if (!startDate.equals(endDate)) {
                    throw new IllegalArgumentException(
                            "For today_or_yesterday, startDate and endDate must be the same day.");
                }
            }
            case "past7days" -> {
                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                if (days != 7) {
                    throw new IllegalArgumentException(
                            "For past7days, the date range must be exactly 7 days.");
                }
            }
            case "past30days" -> {
                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                if (days != 30) {
                    throw new IllegalArgumentException(
                            "For past30days, the date range must be exactly 30 days.");
                }
            }
            default ->
                throw new IllegalArgumentException("Invalid range type: " + rangeType);
        }
    }

    private PastDateRange calculatePastDateRange(LocalDate startDate, LocalDate endDate, String period) {
        return switch (period) {
            case "today_or_yesterday" -> {
                LocalDate pastStartDate = startDate.minusDays(1);
                LocalDate pastEndDate = endDate.minusDays(1);
                yield new PastDateRange(
                        toStartOfDayUtc(pastStartDate),
                        toEndOfDayUtc(pastEndDate));
            }
            case "past7days" -> {
                LocalDate pastStartDate = startDate.minusDays(7);
                LocalDate pastEndDate = startDate.minusDays(1);
                yield new PastDateRange(
                        toStartOfDayUtc(pastStartDate),
                        toEndOfDayUtc(pastEndDate));
            }
            case "past30days" -> {
                LocalDate pastStartDate = startDate.minusDays(30);
                LocalDate pastEndDate = startDate.minusDays(1);
                yield new PastDateRange(
                        toStartOfDayUtc(pastStartDate),
                        toEndOfDayUtc(pastEndDate));
            }
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    private Instant toStartOfDayUtc(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private Instant toEndOfDayUtc(LocalDate date) {
        return date.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();
    }

    private record PastDateRange(Instant pastStart, Instant pastEnd) {
    }
}