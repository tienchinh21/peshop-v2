package xjanua.backend.dto.homepage.MetricDTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.PointsDto;

@Getter
@Setter
public class OrderConversionRateMetricDto {
    private float value;
    private float oldValue;
    private float increment;
    private float changeRate;
    private List<PointsDto> points;
}
