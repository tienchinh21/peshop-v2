package xjanua.backend.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointsDto {
    private LocalDateTime time;
    private String value;
}