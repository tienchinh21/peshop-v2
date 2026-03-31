package xjanua.backend.service.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftCreateDto;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftUpdateDto;
import xjanua.backend.model.Promotion;
import xjanua.backend.model.PromotionGift;
import xjanua.backend.repository.PromotionGiftRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class PromotionGiftService {
    private final PromotionGiftRepo promotionGiftRepo;
    private final ProductService productService;

    public PromotionGiftService(PromotionGiftRepo promotionGiftRepo, ProductService productService) {
        this.productService = productService;
        this.promotionGiftRepo = promotionGiftRepo;
    }

    public PromotionGift fetchById(String id) {
        return promotionGiftRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.PROMOTION_GIFT_NOT_FOUND_MESSAGE));
    }

    private PromotionGift createPromotionGiftNoSave(Promotion promotion, PromotionGiftCreateDto dto, String shopId) {
        var product = productService.fetchById(dto.getProductId());
        productService.checkPermissionOnProduct(product, shopId);
        productService.checkProductDeleted(product);

        return PromotionGift.builder()
                .promotion(promotion)
                .product(product)
                .giftQuantity(dto.getGiftQuantity())
                .build();
    }

    public List<PromotionGift> createPromotionGifts(Promotion promotion, List<PromotionGiftCreateDto> dtoList,
            String shopId) {
        List<PromotionGift> promotionGifts = dtoList.stream()
                .map(dto -> createPromotionGiftNoSave(promotion, dto, shopId))
                .collect(Collectors.toList());

        return promotionGiftRepo.saveAll(promotionGifts);
    }

    private PromotionGift updatePromotionGift(PromotionGiftUpdateDto dto, String shopId) {
        PromotionGift promotionGift = fetchById(dto.getId());
        checkPermissionOnPromotionGift(promotionGift, shopId);

        var productgift = productService.fetchById(dto.getProductId());
        productService.checkPermissionOnProduct(productgift, shopId);
        productService.checkProductDeleted(productgift);

        promotionGift.setProduct(productgift);
        promotionGift.setGiftQuantity(dto.getGiftQuantity());
        return promotionGift;
    }

    public List<PromotionGift> updatePromotionGifts(List<PromotionGiftUpdateDto> dtoList, String shopId) {
        List<PromotionGift> promotionGifts = dtoList.stream()
                .map(dto -> updatePromotionGift(dto, shopId))
                .collect(Collectors.toList());
        return promotionGiftRepo.saveAll(promotionGifts);
    }

    @Transactional
    public void deletePromotionGifts(List<String> ids, String shopId) {
        List<PromotionGift> giftsToSoftDelete = new ArrayList<>();
        List<String> giftsToHardDelete = new ArrayList<>();

        for (String id : ids) {
            PromotionGift promotionGift = fetchById(id);
            checkPermissionOnPromotionGift(promotionGift, shopId);

            if (promotionGift.getPromotion().getStatus() == 3) {
                throw new BadRequestException("Cannot delete gift of a deleted promotion");
            }

            if (Boolean.TRUE.equals(promotionGift.getIsDeleted())) {
                throw new BadRequestException("Gift is already deleted");
            }

            if (promotionGift.getUsages() != null && !promotionGift.getUsages().isEmpty()) {
                promotionGift.setIsDeleted(true);
                giftsToSoftDelete.add(promotionGift);
            } else {
                giftsToHardDelete.add(id);
            }
        }

        if (!giftsToSoftDelete.isEmpty()) {
            promotionGiftRepo.saveAll(giftsToSoftDelete);
        }

        if (!giftsToHardDelete.isEmpty()) {
            promotionGiftRepo.deleteAllById(giftsToHardDelete);
        }
    }

    public void checkPermissionOnPromotionGift(PromotionGift promotionGift, String shopId) {
        if (!promotionGift.getProduct().getShop().getId().equals(shopId)) {
            throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
        }
    }
}