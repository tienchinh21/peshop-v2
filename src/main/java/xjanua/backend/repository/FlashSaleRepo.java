package xjanua.backend.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import xjanua.backend.model.FlashSale;

public interface FlashSaleRepo extends JpaRepository<FlashSale, String>, JpaSpecificationExecutor<FlashSale> {
    List<FlashSale> findAllByCreatedAtBetween(Instant startDate, Instant endDate);
}