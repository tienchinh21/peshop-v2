package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xjanua.backend.model.PromotionRule;

public interface PromotionRuleRepo extends JpaRepository<PromotionRule, String> {

}
