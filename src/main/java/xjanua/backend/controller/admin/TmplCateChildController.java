package xjanua.backend.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.tmplCate.child.TmplCateChildCreateDto;
import xjanua.backend.dto.tmplCate.child.TmplCateChildResponseDto;
import xjanua.backend.dto.tmplCate.child.TmplCateChildUpdateDto;
import xjanua.backend.mapper.TemplateCategoryChildMapper;
import xjanua.backend.service.admin.category.tmplCate.TmplCateChildService;

@RestController
@RequestMapping("/admin/template-category-child")
public class TmplCateChildController {

    private final TmplCateChildService tmplCateChildService;
    private final TemplateCategoryChildMapper templateCategoryChildMapper;

    public TmplCateChildController(TmplCateChildService tmplCateChildService,
            TemplateCategoryChildMapper templateCategoryChildMapper) {
        this.templateCategoryChildMapper = templateCategoryChildMapper;
        this.tmplCateChildService = tmplCateChildService;
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<RestResponse<List<TmplCateChildResponseDto>>> createTemplateCategoryChild(
            @Valid @RequestBody List<TmplCateChildCreateDto> tmplCateChildCreateDto) {
        var dto = tmplCateChildService.createTemplateCategoryChild(tmplCateChildCreateDto).stream()
                .map(templateCategoryChildMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping
    public ResponseEntity<RestResponse<List<TmplCateChildResponseDto>>> updateTemplateCategoryChild(
            @Valid @RequestBody List<TmplCateChildUpdateDto> tmplCateChildUpdateDto) {
        var dto = tmplCateChildService.updateTemplateCategoryChild(tmplCateChildUpdateDto).stream()
                .map(templateCategoryChildMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> deleteTemplateCategoryChild(@PathVariable("id") Integer id) {
        tmplCateChildService.deleteTemplateRegisterCategoryChild(id);
        return ResponseEntity.ok(RestResponse.success(null));
    }
}
