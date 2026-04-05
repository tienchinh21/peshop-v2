package xjanua.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xjanua.backend.model.Product;

public interface ProductRepo extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = { "category", "categoryChild" })
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithDetail(@Param("id") String id);

    @EntityGraph(attributePaths = {
            "category",
            "categoryChild"
    })
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @EntityGraph(attributePaths = { "variants" })
    @Query("SELECT DISTINCT p FROM Product p WHERE p.shop.id = :shopId AND p.status != 2")
    List<Product> findAllByShopIdForExport(@Param("shopId") String shopId);

    Integer countByShop_IdAndStatus(String shopId, Integer status);
}