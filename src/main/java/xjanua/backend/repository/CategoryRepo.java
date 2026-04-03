package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category, String> {

}
