package xjanua.backend.dto.voucher.shop.dash.MetricDTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.PointsDto;

@Getter
@Setter
public class UsageRateMetricsDto {
    private float value;
    private float oldValue;
    private float increment;
    private float changeRate;
    private List<PointsDto> points;
}