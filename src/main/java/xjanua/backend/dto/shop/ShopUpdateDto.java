package xjanua.backend.dto.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopUpdateDto extends ShopCreateDto {
    private Integer status;
}
