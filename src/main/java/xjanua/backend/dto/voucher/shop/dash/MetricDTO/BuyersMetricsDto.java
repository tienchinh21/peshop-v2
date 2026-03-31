package xjanua.backend.dto.voucher.shop.dash.MetricDTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.PointsDto;

@Getter
@Setter
public class BuyersMetricsDto {
    private int value;
    private int oldValue;
    private int increment;
    private float changeRate;
    private List<PointsDto> points;
}