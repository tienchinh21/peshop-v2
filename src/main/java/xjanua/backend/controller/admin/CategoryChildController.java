package xjanua.backend.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.category.child.CategoryChildCreateDto;
import xjanua.backend.dto.category.child.CategoryChildResponseDto;
import xjanua.backend.dto.category.child.CategoryChildResponseDtoDetail;
import xjanua.backend.dto.category.child.UpdateCategoryChildDto;
import xjanua.backend.mapper.CategoryChildMapper;
import xjanua.backend.service.admin.category.CategoryChildService;

@RestController
@RequestMapping("/admin/category-child")
public class CategoryChildController {

    private final CategoryChildService categoryChildService;
    private final CategoryChildMapper categoryChildMapper;

    public CategoryChildController(CategoryChildService categoryChildService, CategoryChildMapper categoryChildMapper) {
        this.categoryChildMapper = categoryChildMapper;
        this.categoryChildService = categoryChildService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<CategoryChildResponseDtoDetail>> getCategoryChildDetail(
            @PathVariable("id") String id) {
        var entity = categoryChildService.fetchById(id);
        var dto = categoryChildMapper.toDtoDetail(entity);
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<RestResponse<List<CategoryChildResponseDto>>> createCategoryChild(
            @Valid @RequestBody List<CategoryChildCreateDto> categoryChildCreateDto) {
        var dto = categoryChildService.createCategoryChild(categoryChildCreateDto).stream()
                .map(categoryChildMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping
    public ResponseEntity<RestResponse<List<CategoryChildResponseDto>>> updateCategoryChild(
            @Valid @RequestBody List<UpdateCategoryChildDto> updateCategoryChildDto) {
        var dto = categoryChildService.updateCategoryChild(updateCategoryChildDto).stream()
                .map(categoryChildMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> deleteCategoryChild(@PathVariable("id") String id) {
        categoryChildService.deleteCategoryChild(id);
        return ResponseEntity.ok(RestResponse.success(null));
    }
}
