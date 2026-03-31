package xjanua.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import xjanua.backend.model.Variant;

public interface VariantRepo extends JpaRepository<Variant, Integer> {
    @EntityGraph(attributePaths = { "variantValues.propertyValue" })
    Optional<Variant> findDetailById(Integer id);
}
