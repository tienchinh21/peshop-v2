package xjanua.backend.dto.homepage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class todolistShopDto {
    private int waitingForDelivery;
    private int processed;
    private int returnsAndCancellations;
    private int productlocked;
}
