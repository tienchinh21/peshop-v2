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
import xjanua.backend.dto.voucher.shop.dash.CampaignMetricsDto;
import xjanua.backend.dto.voucher.shop.dash.OrderMetricsViewDto;
import xjanua.backend.dto.voucher.shop.dash.MetricDTO.BuyersMetricsDto;
import xjanua.backend.dto.voucher.shop.dash.MetricDTO.OrdersMetricsDto;
import xjanua.backend.dto.voucher.shop.dash.MetricDTO.SalesMetricsDto;
import xjanua.backend.dto.voucher.shop.dash.MetricDTO.UsageRateMetricsDto;
import xjanua.backend.service.RedisService;

@Service
@RequiredArgsConstructor
public class VoucherShopDashService {

        private final OrderService orderService;
        private final ShopService shopService;
        private final RedisService redisService;

        public CampaignMetricsDto getVoucherShopDashboardSummary(LocalDate startDate, LocalDate endDate,
                        String period) {
                String shopId = shopService.fetchByUserLogin().getId();

                validateRange(startDate, endDate, period);

                String cacheKey = "peshop:vouchershop:dashboard:" + shopId + ":" + startDate + ":" + endDate + ":"
                                + period;
                try {
                        CampaignMetricsDto cachedDashboard = redisService.getObject(cacheKey, CampaignMetricsDto.class);
                        if (cachedDashboard != null) {
                                return cachedDashboard;
                        }
                } catch (Exception e) {
                }

                Instant startInstant = toStartOfDayUtc(startDate);
                Instant endInstant = toEndOfDayUtc(endDate);

                List<OrderMetricsViewDto> currentOrders = orderService.fetchByShopIdAndStatusOrderAndCreatedAtBetween(
                                startInstant, endInstant, List.of(1, 3, 4, 5), shopId, true);

                PastDateRange pastRange = calculatePastDateRange(startDate, endDate, period);
                List<OrderMetricsViewDto> pastOrders = orderService.fetchByShopIdAndStatusOrderAndCreatedAtBetween(
                                pastRange.pastStart(), pastRange.pastEnd(), List.of(1, 3, 4, 5), shopId, false);

                CampaignMetricsDto dto = new CampaignMetricsDto();

                dto.setSales(createSalesMetrics(currentOrders, pastOrders, startDate, endDate, period));
                dto.setOrders(createOrdersMetrics(currentOrders, pastOrders, startDate, endDate, period));
                dto.setUsageRate(createUsageRateMetrics(currentOrders, pastOrders, startDate, endDate, period));
                dto.setBuyers(createBuyersMetrics(currentOrders, pastOrders, startDate, endDate, period));

                try {
                        redisService.setObject(cacheKey, dto, 180);
                } catch (Exception e) {
                }

                return dto;
        }

        private SalesMetricsDto createSalesMetrics(List<OrderMetricsViewDto> currentOrders,
                        List<OrderMetricsViewDto> pastOrders,
                        LocalDate startDate, LocalDate endDate, String period) {
                BigDecimal value = currentOrders.stream()
                                .map(o -> o.getOriginalPrice().subtract(o.getShopVoucherDiscount()))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal oldValue = pastOrders.stream()
                                .map(o -> o.getOriginalPrice().subtract(o.getShopVoucherDiscount()))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal increment = value.subtract(oldValue);
                float change_rate = oldValue.compareTo(BigDecimal.ZERO) == 0
                                ? (value.compareTo(BigDecimal.ZERO) > 0 ? 100f : 0f)
                                : increment.divide(oldValue, 2, java.math.RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100)).floatValue();

                List<PointsDto> points = createPointsBase(startDate, endDate, period);
                fillPoints(currentOrders, points, startDate, endDate, period,
                                o -> o.getOriginalPrice().subtract(o.getShopVoucherDiscount()));

                SalesMetricsDto dto = new SalesMetricsDto();
                dto.setValue(value);
                dto.setOldValue(oldValue);
                dto.setIncrement(increment);
                dto.setChangeRate(change_rate);
                dto.setPoints(points);
                return dto;
        }

        private OrdersMetricsDto createOrdersMetrics(List<OrderMetricsViewDto> currentOrders,
                        List<OrderMetricsViewDto> pastOrders,
                        LocalDate startDate, LocalDate endDate, String period) {
                int value = currentOrders.size();
                int oldValue = pastOrders.size();
                int increment = value - oldValue;
                float change_rate = oldValue == 0 ? (value > 0 ? 100f : 0f) : ((float) increment / oldValue * 100);

                List<PointsDto> points = createPointsBase(startDate, endDate, period);
                fillPoints(currentOrders, points, startDate, endDate, period, o -> BigDecimal.ONE);

                OrdersMetricsDto dto = new OrdersMetricsDto();
                dto.setValue(value);
                dto.setOldValue(oldValue);
                dto.setIncrement(increment);
                dto.setChangeRate(change_rate);
                dto.setPoints(points);
                return dto;
        }

        private UsageRateMetricsDto createUsageRateMetrics(
                        List<OrderMetricsViewDto> currentOrders,
                        List<OrderMetricsViewDto> pastOrders,
                        LocalDate startDate,
                        LocalDate endDate,
                        String period) {

                long currentOrdersWithVoucher = currentOrders.stream()
                                .filter(o -> o.getShopVoucherDiscount() != null
                                                && o.getShopVoucherDiscount().compareTo(BigDecimal.ZERO) > 0)
                                .count();

                long pastOrdersWithVoucher = pastOrders.stream()
                                .filter(o -> o.getShopVoucherDiscount() != null
                                                && o.getShopVoucherDiscount().compareTo(BigDecimal.ZERO) > 0)
                                .count();

                float value = currentOrders.isEmpty() ? 0f
                                : (float) currentOrdersWithVoucher / currentOrders.size() * 100;

                float oldValue = pastOrders.isEmpty() ? 0f
                                : (float) pastOrdersWithVoucher / pastOrders.size() * 100;

                float increment = value - oldValue;

                float change_rate = oldValue == 0 ? (value > 0 ? 100f : 0f)
                                : (increment / oldValue * 100);

                List<PointsDto> points = createPointsBase(startDate, endDate, period);

                fillPoints(currentOrders, points, startDate, endDate, period,
                                o -> (o.getShopVoucherDiscount() != null
                                                && o.getShopVoucherDiscount().compareTo(BigDecimal.ZERO) > 0)
                                                                ? BigDecimal.ONE
                                                                : BigDecimal.ZERO);

                UsageRateMetricsDto dto = new UsageRateMetricsDto();
                dto.setValue(value);
                dto.setOldValue(oldValue);
                dto.setIncrement(increment);
                dto.setChangeRate(change_rate);
                dto.setPoints(points);
                return dto;
        }

        private BuyersMetricsDto createBuyersMetrics(List<OrderMetricsViewDto> currentOrders,
                        List<OrderMetricsViewDto> pastOrders,
                        LocalDate startDate, LocalDate endDate, String period) {
                int value = (int) currentOrders.stream().map(o -> o.getUserId()).distinct().count();
                int oldValue = (int) pastOrders.stream().map(o -> o.getUserId()).distinct().count();
                int increment = value - oldValue;
                float change_rate = oldValue == 0 ? (value > 0 ? 100f : 0f) : ((float) increment / oldValue * 100);

                List<PointsDto> points = createPointsBase(startDate, endDate, period);
                fillPoints(currentOrders, points, startDate, endDate, period, o -> BigDecimal.ONE);

                BuyersMetricsDto dto = new BuyersMetricsDto();
                dto.setValue(value);
                dto.setOldValue(oldValue);
                dto.setIncrement(increment);
                dto.setChangeRate(change_rate);
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
