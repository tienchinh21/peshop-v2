package xjanua.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.Review;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {
    @EntityGraph(attributePaths = {
            "user",
            "product",
            "variant",
            "order"
    })
    Page<Review> findAll(Specification<Review> spec, Pageable pageable);
}
