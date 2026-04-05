package xjanua.backend.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.tmplCate.TmplCateCreateDto;
import xjanua.backend.dto.tmplCate.TmplCateResponseDto;
import xjanua.backend.dto.tmplCate.TmplCateUpdateDto;
import xjanua.backend.mapper.TemplateCategoryMapper;
import xjanua.backend.service.admin.category.tmplCate.TmplCateService;

@RestController
@RequestMapping("/admin/template-category")
public class TmlpCateController {

    private final TmplCateService tmplCateService;
    private final TemplateCategoryMapper templateCategoryMapper;

    public TmlpCateController(TmplCateService tmplCateService, TemplateCategoryMapper templateCategoryMapper) {
        this.tmplCateService = tmplCateService;
        this.templateCategoryMapper = templateCategoryMapper;
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<RestResponse<List<TmplCateResponseDto>>> createTemplateCategory(
            @Valid @RequestBody List<TmplCateCreateDto> tmplCateCreateDto) {
        var dto = tmplCateService.createTemplateCategory(tmplCateCreateDto).stream()
                .map(templateCategoryMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping
    public ResponseEntity<RestResponse<List<TmplCateResponseDto>>> updateTemplateCategory(
            @Valid @RequestBody List<TmplCateUpdateDto> tmplCateUpdateDto) {
        var dto = tmplCateService.updateTemplateCategory(tmplCateUpdateDto).stream()
                .map(templateCategoryMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(RestResponse.success(dto));
    }
}
