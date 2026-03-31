package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.TemplateCategory;

@Repository
public interface TemplateRegisterCategoryRepo extends JpaRepository<TemplateCategory, Integer> {

}
