package xjanua.backend.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.propertyProduct.PropertyProductCreateDto;
import xjanua.backend.dto.propertyProduct.PropertyProductResponse;
import xjanua.backend.dto.propertyProduct.PropertyProductUpdateDto;
import xjanua.backend.mapper.PropertyProductMapper;
import xjanua.backend.service.admin.PropertyProductService;

@RestController
@RequestMapping("/admin/property-product")
public class PropertyProductController {

    private final PropertyProductService propertyProductService;
    private final PropertyProductMapper propertyMapper;

    public PropertyProductController(PropertyProductService propertyProductService,
            PropertyProductMapper propertyMapper) {
        this.propertyMapper = propertyMapper;
        this.propertyProductService = propertyProductService;
    }

    @GetMapping
    public ResponseEntity<RestResponse<List<PropertyProductResponse>>> getAllPropertyProduct() {
        var dto = propertyProductService.fetchAll().stream()
                .map(propertyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<RestResponse<List<PropertyProductResponse>>> createPropertyProduct(
            @Valid @RequestBody List<PropertyProductCreateDto> propertyProductCreateDto) {
        var dto = propertyProductService.createPropertyProduct(propertyProductCreateDto).stream()
                .map(propertyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping
    public ResponseEntity<RestResponse<List<PropertyProductResponse>>> updatePropertyProduct(
            @Valid @RequestBody List<PropertyProductUpdateDto> propertyProductUpdateDto) {
        var dto = propertyProductService.updatePropertyProduct(propertyProductUpdateDto).stream()
                .map(propertyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(RestResponse.success(dto));
    }
}
