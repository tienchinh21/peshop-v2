package xjanua.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import xjanua.backend.model.FlashSaleProduct;

public interface FlashSaleProductRepo extends JpaRepository<FlashSaleProduct, String> {
    @Query("""
                SELECT fsp
                FROM FlashSaleProduct fsp
                JOIN FETCH fsp.product p
                JOIN FETCH fsp.flashSale fs
                WHERE p.shop.id = :shopId
            """)
    List<FlashSaleProduct> findAllByShopIdWithRelations(@Param("shopId") String shopId);

    @Query("SELECT COUNT(fsp) FROM FlashSaleProduct fsp WHERE fsp.flashSale.id = :flashSaleId")
    Long countByFlashSaleId(@Param("flashSaleId") String flashSaleId);

    @Query("SELECT COALESCE(SUM(fsp.usedQuantity), 0) FROM FlashSaleProduct fsp WHERE fsp.flashSale.id = :flashSaleId")
    Long sumUsedQuantityByFlashSaleId(@Param("flashSaleId") String flashSaleId);

    @EntityGraph(attributePaths = { "product" })
    @Query("SELECT fsp FROM FlashSaleProduct fsp WHERE fsp.flashSale.id = :flashSaleId")
    List<FlashSaleProduct> findAllByFlashSaleIdWithProduct(@Param("flashSaleId") String flashSaleId);
}