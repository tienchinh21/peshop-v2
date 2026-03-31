package xjanua.backend.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.category.CategoryCreateDto;
import xjanua.backend.dto.category.CategoryResponseDetailDto;
import xjanua.backend.dto.category.CategoryResponseDto;
import xjanua.backend.dto.category.CategoryUpdateDto;
import xjanua.backend.mapper.CategoryMapper;
import xjanua.backend.service.admin.category.CategoryService;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<CategoryResponseDetailDto>> getCategoryDetail(@PathVariable("id") String id) {
        var entity = categoryService.fetchById(id);
        var dto = categoryMapper.toDtoDetail(entity);
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @GetMapping
    public ResponseEntity<RestResponse<List<CategoryResponseDto>>> getAllCategory() {
        var categories = categoryService.fetchAll();
        var dto = categories.stream()
                .map(categoryMapper::toDto)
                .toList();
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<RestResponse<CategoryResponseDto>> createCategory(
            @Valid @RequestBody CategoryCreateDto categoryCreateDto) {
        var entity = categoryService.createCategory(categoryCreateDto);
        var dto = categoryMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<CategoryResponseDto>> updateCategory(
            @PathVariable("id") String id,
            @Valid @RequestBody CategoryUpdateDto categoryUpdateDto) {
        var entity = categoryService.updateCategory(id, categoryUpdateDto);
        var dto = categoryMapper.toDto(entity);
        return ResponseEntity.ok(RestResponse.success(dto));
    }
}
