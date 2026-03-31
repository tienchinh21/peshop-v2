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
import xjanua.backend.dto.attributeTemplate.AttributeTemplateCreateDto;
import xjanua.backend.dto.attributeTemplate.AttributeTemplateResponseDto;
import xjanua.backend.dto.attributeTemplate.AttributeTemplateUpdateDto;
import xjanua.backend.mapper.AttributeTemplateMapper;
import xjanua.backend.service.admin.category.tmplCate.AttributeTemplateService;

@RestController
@RequestMapping("/admin/attribute-template")
public class AttributeTemplateController {

        private final AttributeTemplateService attributeTemplateService;
        private final AttributeTemplateMapper attributeTemplateMapper;

        public AttributeTemplateController(AttributeTemplateService attributeTemplateService,
                        AttributeTemplateMapper attributeTemplateMapper) {
                this.attributeTemplateService = attributeTemplateService;
                this.attributeTemplateMapper = attributeTemplateMapper;
        }

        @PreAuthorize("hasAuthority('Admin')")
        @PostMapping
        public ResponseEntity<RestResponse<List<AttributeTemplateResponseDto>>> createAttributeTemplate(
                        @Valid @RequestBody List<AttributeTemplateCreateDto> attributeTemplateCreateDto) {
                var dto = attributeTemplateService.createAttributeTemplate(attributeTemplateCreateDto).stream()
                                .map(attributeTemplateMapper::toDto)
                                .collect(Collectors.toList());
                return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.success(dto));
        }

        @PreAuthorize("hasAuthority('Admin')")
        @PutMapping
        public ResponseEntity<RestResponse<List<AttributeTemplateResponseDto>>> updateAttributeTemplate(
                        @Valid @RequestBody List<AttributeTemplateUpdateDto> attributeTemplateUpdateDto) {
                var dto = attributeTemplateService.updateAttributeTemplate(attributeTemplateUpdateDto).stream()
                                .map(attributeTemplateMapper::toDto)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(RestResponse.success(dto));
        }
}
