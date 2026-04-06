package xjanua.backend.service.admin.category.tmplCate;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import xjanua.backend.dto.tmplCate.child.TmplCateChildCreateDto;
import xjanua.backend.dto.tmplCate.child.TmplCateChildUpdateDto;
import xjanua.backend.model.TemplateCategoryChild;
import xjanua.backend.repository.templateRegisterCategoryChildRepo;
import xjanua.backend.service.admin.category.CategoryChildService;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class TmplCateChildService {

    private final templateRegisterCategoryChildRepo templateRegisterCategoryChildRepo;
    private final CategoryChildService categoryChildService;

    public TmplCateChildService(templateRegisterCategoryChildRepo templateRegisterCategoryChildRepo,
            CategoryChildService categoryChildService) {
        this.templateRegisterCategoryChildRepo = templateRegisterCategoryChildRepo;
        this.categoryChildService = categoryChildService;
    }

    public TemplateCategoryChild fetchById(Integer id) {
        return templateRegisterCategoryChildRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResponseConstants.TEMPLATE_REGISTER_CATEGORY_CHILD_NOT_FOUND_MESSAGE));
    }

    public List<TemplateCategoryChild> createTemplateCategoryChild(List<TmplCateChildCreateDto> requests) {
        return requests.stream()
                .map(request -> templateRegisterCategoryChildRepo.save(TemplateCategoryChild.builder()
                        .name(request.getName())
                        .categoryChild(categoryChildService.fetchById(request.getCategoryChildId()))
                        .build()))
                .collect(Collectors.toList());
    }

    public List<TemplateCategoryChild> updateTemplateCategoryChild(List<TmplCateChildUpdateDto> requests) {
        return requests.stream()
                .map(request -> {
                    TemplateCategoryChild templateRegisterCategoryChild = fetchById(request.getId());
                    templateRegisterCategoryChild.setName(request.getName());
                    return templateRegisterCategoryChildRepo.save(templateRegisterCategoryChild);
                })
                .collect(Collectors.toList());
    }

    public void deleteTemplateRegisterCategoryChild(Integer id) {
        TemplateCategoryChild templateRegisterCategoryChild = fetchById(id);
        templateRegisterCategoryChildRepo.delete(templateRegisterCategoryChild);
    }
}
