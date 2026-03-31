package xjanua.backend.dto.voucher.shop.dash.MetricDTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.PointsDto;

@Getter
@Setter
public class SalesMetricsDto {
    private BigDecimal value;
    private BigDecimal oldValue;
    private BigDecimal increment;
    private float changeRate;
    private List<PointsDto> points;
}