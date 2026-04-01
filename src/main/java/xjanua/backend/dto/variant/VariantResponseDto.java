package xjanua.backend.dto.variant;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.variantValue.VariantValueResponseDto;

@Getter
@Setter
public class VariantResponseDto {
    private Integer id;
    private BigDecimal price;
    private Integer quantity;
    private Integer status;
    private List<VariantValueResponseDto> variantValues;
}