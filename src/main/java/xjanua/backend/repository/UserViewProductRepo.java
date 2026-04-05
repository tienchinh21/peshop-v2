package xjanua.backend.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.UserViewProduct;

@Repository
public interface UserViewProductRepo extends JpaRepository<UserViewProduct, Integer> {

    @Query("""
                SELECT uvp
                FROM UserViewProduct uvp
                JOIN uvp.product p
                WHERE p.shop.id = :shopId
                    AND uvp.createdAt BETWEEN :startDate AND :endDate
            """)
    List<UserViewProduct> getAllViewsByShopAndCreatedAtBetween(
            String shopId,
            Instant startDate,
            Instant endDate);
}