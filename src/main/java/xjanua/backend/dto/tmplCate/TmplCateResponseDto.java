package xjanua.backend.dto.tmplCate;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xjanua.backend.dto.attributeTemplate.AttributeTemplateResponseDto;

@Getter
@Setter
public class TmplCateResponseDto {
    private Integer id;
    private String name;
    private List<AttributeTemplateResponseDto> attributeTemplates;
}