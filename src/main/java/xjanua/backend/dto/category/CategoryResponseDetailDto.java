package xjanua.backend.dto.category;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.category.child.CategoryChildResponseDto;

@Getter
@Setter
public class CategoryResponseDetailDto {

    private String id;
    private String name;
    private String type;
    private List<CategoryChildResponseDto> categoryChildren;
}