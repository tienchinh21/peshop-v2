package xjanua.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.Shop;

@Repository
public interface ShopRepo extends JpaRepository<Shop, String> {
    Optional<Shop> findByUserId(String userId);
}
