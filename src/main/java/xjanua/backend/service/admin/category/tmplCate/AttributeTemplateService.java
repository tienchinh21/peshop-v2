package xjanua.backend.service.admin.category.tmplCate;

import java.util.List;

import org.springframework.stereotype.Service;

import xjanua.backend.dto.attributeTemplate.AttributeTemplateCreateDto;
import xjanua.backend.dto.attributeTemplate.AttributeTemplateUpdateDto;
import xjanua.backend.model.AttributeTemplate;
import xjanua.backend.repository.AttributeTemplateRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class AttributeTemplateService {

        private final AttributeTemplateRepo attributeTemplateRepo;
        private final TmplCateService tmplCateService;
        private final TmplCateChildService tmplCateChildService;

        public AttributeTemplateService(AttributeTemplateRepo attributeTemplateRepo, TmplCateService tmplCateService,
                        TmplCateChildService tmplCateChildService) {
                this.tmplCateService = tmplCateService;
                this.tmplCateChildService = tmplCateChildService;
                this.attributeTemplateRepo = attributeTemplateRepo;
        }

        public AttributeTemplate fetchById(Integer id) {
                return attributeTemplateRepo.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ResponseConstants.ATTRIBUTE_TEMPLATE_NOT_FOUND_MESSAGE));
        }

        public List<AttributeTemplate> createAttributeTemplate(List<AttributeTemplateCreateDto> requests) {
                return requests.stream()
                                .map(request -> {
                                        validateTemplateTarget(request);

                                        AttributeTemplate template = AttributeTemplate.builder()
                                                        .name(request.getName())
                                                        .templateCategory(
                                                                        request.getTemplateCategoryId() != null
                                                                                        ? tmplCateService.fetchById(
                                                                                                        request.getTemplateCategoryId())
                                                                                        : null)
                                                        .templateCategoryChild(
                                                                        request.getTemplateCategoryChildId() != null
                                                                                        ? tmplCateChildService
                                                                                                        .fetchById(request
                                                                                                                        .getTemplateCategoryChildId())
                                                                                        : null)
                                                        .build();

                                        return attributeTemplateRepo.save(template);
                                })
                                .toList();
        }

        public List<AttributeTemplate> updateAttributeTemplate(List<AttributeTemplateUpdateDto> requests) {
                return requests.stream()
                                .map(request -> {
                                        AttributeTemplate template = fetchById(request.getId());
                                        template.setName(request.getName());
                                        template.setTemplateCategory(template.getTemplateCategory());
                                        template.setTemplateCategoryChild(template.getTemplateCategoryChild());
                                        return attributeTemplateRepo.save(template);
                                })
                                .toList();
        }

        private void validateTemplateTarget(AttributeTemplateCreateDto request) {
                boolean hasCategory = request.getTemplateCategoryId() != null;
                boolean hasCategoryChild = request.getTemplateCategoryChildId() != null;

                if (hasCategory == hasCategoryChild) { // cả hai cùng true hoặc cùng false
                        throw new IllegalArgumentException(
                                        "Exactly one of templateCategoryId or templateCategoryChildId must be provided");
                }
        }
}