package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.CategoryChild;

@Repository
public interface CategoryChildRepo extends JpaRepository<CategoryChild, String> {

}
