package xjanua.backend.dto.tmplCate.child;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.attributeTemplate.AttributeTemplateResponseDto;

@Getter
@Setter
public class TmplCateChildResponseDto {
        private Integer id;
        private String name;
        private List<AttributeTemplateResponseDto> attributeTemplates;
}