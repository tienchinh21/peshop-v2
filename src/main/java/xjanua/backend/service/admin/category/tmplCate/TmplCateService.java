package xjanua.backend.service.admin.category.tmplCate;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import xjanua.backend.dto.tmplCate.TmplCateCreateDto;
import xjanua.backend.dto.tmplCate.TmplCateUpdateDto;
import xjanua.backend.model.TemplateCategory;
import xjanua.backend.repository.TemplateRegisterCategoryRepo;
import xjanua.backend.service.admin.category.CategoryService;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class TmplCateService {

    private final TemplateRegisterCategoryRepo templateRegisterCategoryRepo;
    private final CategoryService categoryService;

    public TmplCateService(TemplateRegisterCategoryRepo templateRegisterCategoryRepo,
            CategoryService categoryService) {
        this.categoryService = categoryService;
        this.templateRegisterCategoryRepo = templateRegisterCategoryRepo;
    }

    public TemplateCategory fetchById(Integer id) {
        return templateRegisterCategoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResponseConstants.TEMPLATE_REGISTER_CATEGORY_NOT_FOUND_MESSAGE));
    }

    public List<TemplateCategory> createTemplateCategory(List<TmplCateCreateDto> requests) {
        return requests.stream()
                .map(request -> templateRegisterCategoryRepo.save(TemplateCategory.builder()
                        .name(request.getName())
                        .category(categoryService.fetchById(request.getCategoryId()))
                        .build()))
                .collect(Collectors.toList());
    }

    public List<TemplateCategory> updateTemplateCategory(List<TmplCateUpdateDto> requests) {
        return requests.stream()
                .map(request -> {
                    TemplateCategory templateRegisterCategory = fetchById(request.getId());
                    templateRegisterCategory.setName(request.getName());
                    return templateRegisterCategoryRepo.save(templateRegisterCategory);
                })
                .collect(Collectors.toList());
    }
}
