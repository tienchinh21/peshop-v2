package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import xjanua.backend.model.Promotion;

public interface PromotionRepo extends JpaRepository<Promotion, String>, JpaSpecificationExecutor<Promotion> {

}