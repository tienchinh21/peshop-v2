package xjanua.backend.service.admin.category;

import java.util.List;

import org.springframework.stereotype.Service;

import xjanua.backend.dto.category.CategoryCreateDto;
import xjanua.backend.dto.category.CategoryUpdateDto;
import xjanua.backend.model.Category;
import xjanua.backend.repository.CategoryRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Category fetchById(String categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.CATEGORY_NOT_FOUND_MESSAGE));
    }

    public List<Category> fetchAll() {
        return categoryRepo.findAll();
    }

    public Category createCategory(CategoryCreateDto request) {
        Category category = categoryRepo.save(Category.builder()
                .name(request.getName())
                .type(request.getType())
                .build());
        return category;
    }

    public Category updateCategory(String categoryId, CategoryUpdateDto request) {
        Category category = fetchById(categoryId);
        category.setName(request.getName());
        return categoryRepo.save(category);
    }
}
