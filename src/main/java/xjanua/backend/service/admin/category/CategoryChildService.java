package xjanua.backend.service.admin.category;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import xjanua.backend.dto.category.child.CategoryChildCreateDto;
import xjanua.backend.dto.category.child.UpdateCategoryChildDto;
import xjanua.backend.model.Category;
import xjanua.backend.model.CategoryChild;
import xjanua.backend.repository.CategoryChildRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class CategoryChildService {

    private final CategoryChildRepo categoryChildRepo;
    private final CategoryService categoryService;

    public CategoryChildService(CategoryChildRepo categoryChildRepo, CategoryService categoryService) {
        this.categoryService = categoryService;
        this.categoryChildRepo = categoryChildRepo;
    }

    public CategoryChild fetchById(String categoryChildId) {
        return categoryChildRepo.findById(categoryChildId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.CATEGORY_CHILD_NOT_FOUND_MESSAGE));
    }

    public Category fetchCategoryByCategoryChildId(String categoryChildId) {
        CategoryChild categoryChild = fetchById(categoryChildId);
        return categoryChild.getCategory();
    }

    public List<CategoryChild> createCategoryChild(List<CategoryChildCreateDto> requests) {
        return requests.stream()
                .map(request -> categoryChildRepo.save(CategoryChild.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .category(categoryService.fetchById(request.getCategoryId()))
                        .build()))
                .collect(Collectors.toList());
    }

    public List<CategoryChild> updateCategoryChild(List<UpdateCategoryChildDto> requests) {
        return requests.stream()
                .map(request -> {
                    CategoryChild categoryChild = fetchById(request.getId());
                    categoryChild.setName(request.getName());
                    categoryChild.setDescription(request.getDescription());
                    return categoryChildRepo.save(categoryChild);
                })
                .collect(Collectors.toList());
    }

    public void deleteCategoryChild(String categoryChildId) {
        CategoryChild categoryChild = fetchById(categoryChildId);
        categoryChildRepo.delete(categoryChild);
    }
}
