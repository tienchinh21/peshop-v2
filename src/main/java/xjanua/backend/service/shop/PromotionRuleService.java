package xjanua.backend.service.shop;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleCreateDto;
import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleUpdateDto;
import xjanua.backend.model.Promotion;
import xjanua.backend.model.PromotionRule;
import xjanua.backend.repository.PromotionRuleRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class PromotionRuleService {
    private final PromotionRuleRepo promotionRuleRepo;
    private final ProductService productService;

    public PromotionRuleService(PromotionRuleRepo promotionRuleRepo, ProductService productService) {
        this.productService = productService;
        this.promotionRuleRepo = promotionRuleRepo;
    }

    public PromotionRule fetchById(String id) {
        return promotionRuleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.PROMOTION_RULE_NOT_FOUND_MESSAGE));
    }

    private PromotionRule createPromotionRuleNoSave(Promotion promotion, PromotionRuleCreateDto dto, String shopId) {
        var product = productService.fetchById(dto.getProductId());
        productService.checkPermissionOnProduct(product, shopId);
        productService.checkProductDeleted(product);

        return PromotionRule.builder()
                .promotion(promotion)
                .product(product)
                .quantity(dto.getQuantity())
                .build();
    }

    public List<PromotionRule> createPromotionRules(Promotion promotion, List<PromotionRuleCreateDto> dtoList,
            String shopId) {
        List<PromotionRule> promotionRules = dtoList.stream()
                .map(dto -> createPromotionRuleNoSave(promotion, dto, shopId))
                .collect(Collectors.toList());

        return promotionRuleRepo.saveAll(promotionRules);
    }

    private PromotionRule updatePromotionRule(PromotionRuleUpdateDto dto, String shopId) {
        PromotionRule promotionRule = fetchById(dto.getId());
        checkPermissionOnPromotionRule(promotionRule, shopId);
        var product = productService.fetchById(dto.getProductId());
        productService.checkPermissionOnProduct(product, shopId);
        productService.checkProductDeleted(product);

        promotionRule.setProduct(product);
        promotionRule.setQuantity(dto.getQuantity());
        return promotionRule;
    }

    public List<PromotionRule> updatePromotionRules(List<PromotionRuleUpdateDto> dtoList, String shopId) {
        List<PromotionRule> promotionRules = dtoList.stream()
                .map(dto -> updatePromotionRule(dto, shopId))
                .collect(Collectors.toList());
        return promotionRuleRepo.saveAll(promotionRules);
    }

    public void deletePromotionRules(List<String> ids, String shopId) {
        for (String id : ids) {
            PromotionRule promotionRule = fetchById(id);
            checkPermissionOnPromotionRule(promotionRule, shopId);

            if (promotionRule.getPromotion().getStatus() == 3) {
                throw new BadRequestException("Cannot delete rule of a deleted promotion");
            }
        }
        promotionRuleRepo.deleteAllById(ids);
    }

    public void checkPermissionOnPromotionRule(PromotionRule promotionRule, String shopId) {
        if (!promotionRule.getProduct().getShop().getId().equals(shopId)) {
            throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
        }
    }
}