package xjanua.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xjanua.backend.model.ImageProduct;

public interface ImageProductRepo extends JpaRepository<ImageProduct, String> {
    List<ImageProduct> findByProductId(String productId);

    Optional<ImageProduct> findByIdAndProduct_Shop_Id(String imageProductId, String shopId);

    // → tìm tất cả các bản ghi trong bảng image_product có product_id = ?.
    // → sắp xếp theo sort_order tăng dần.
    List<ImageProduct> findByProductIdOrderBySortOrderAsc(String productId);
}
