package xjanua.backend.dto.homepage;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.homepage.MetricDTO.CancellationSalesMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.OrderConversionRateMetricDto;
import xjanua.backend.dto.homepage.MetricDTO.OrdersCancelledMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.OrdersMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.ProductClicksMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.RevenuePerOrderMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.SalesMetricsDto;
import xjanua.backend.dto.homepage.MetricDTO.VisitsMetricsDto;

@Getter
@Setter
public class dashboardDto {
    private SalesMetricsDto sales;
    private VisitsMetricsDto visits;
    private OrdersMetricsDto orders;
    private OrdersCancelledMetricsDto ordersCancelled;
    private ProductClicksMetricsDto productClicks;
    private OrderConversionRateMetricDto orderConversionRate;
    private RevenuePerOrderMetricsDto revenuePerOrder;
    private CancellationSalesMetricsDto cancellationSales;
}