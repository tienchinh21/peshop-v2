package xjanua.backend.dto.propertyValue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyValueResponseDto {
    private String id;
    private String value;
    private String imgUrl;
    private Integer level;
}