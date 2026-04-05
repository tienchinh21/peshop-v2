package xjanua.backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductDtoForBot {
    private String id;
    private String name;
    private String imgMain;
}