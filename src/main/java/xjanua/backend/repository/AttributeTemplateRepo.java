package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.AttributeTemplate;

@Repository
public interface AttributeTemplateRepo extends JpaRepository<AttributeTemplate, Integer> {

}
