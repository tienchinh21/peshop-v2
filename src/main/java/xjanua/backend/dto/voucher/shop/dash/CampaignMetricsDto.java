package xjanua.backend.dto.voucher.shop.dash;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.voucher.shop.dash.MetricDTO.BuyersMetricsDto;
import xjanua.backend.dto.voucher.shop.dash.MetricDTO.OrdersMetricsDto;
import xjanua.backend.dto.voucher.shop.dash.MetricDTO.SalesMetricsDto;
import xjanua.backend.dto.voucher.shop.dash.MetricDTO.UsageRateMetricsDto;

@Getter
@Setter
public class CampaignMetricsDto {
    private SalesMetricsDto sales;
    private OrdersMetricsDto orders;
    private UsageRateMetricsDto usageRate;
    private BuyersMetricsDto buyers;
}