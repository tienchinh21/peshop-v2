package xjanua.backend.dto.homepage.MetricDTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.PointsDto;

@Getter
@Setter
public class OrdersMetricsDto {
    private int value;
    private int oldValue;
    private int increment;
    private float changeRate;
    private List<PointsDto> points;
}