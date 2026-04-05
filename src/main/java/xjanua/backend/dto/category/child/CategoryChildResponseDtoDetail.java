package xjanua.backend.dto.category.child;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.tmplCate.TmplCateResponseDto;
import xjanua.backend.dto.tmplCate.child.TmplCateChildResponseDto;

@Getter
@Setter
public class CategoryChildResponseDtoDetail {
        private String id;
        private String name;
        private String description;
        private List<TmplCateResponseDto> templateCategories;
        private List<TmplCateChildResponseDto> templateCategoryChildren;
}