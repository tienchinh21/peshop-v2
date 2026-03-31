package xjanua.backend.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.UserViewShop;

@Repository
public interface UserViewShopRepo extends JpaRepository<UserViewShop, Integer> {
    List<UserViewShop> findAllByShop_IdAndCreatedAtBetween(String shopId, Instant start, Instant end);
}