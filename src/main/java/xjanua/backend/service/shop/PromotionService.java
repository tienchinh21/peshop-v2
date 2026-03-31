package xjanua.backend.service.shop;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.Promotion.PromotionCreateDto;
import xjanua.backend.dto.Promotion.PromotionMapEntityCreateDto;
import xjanua.backend.dto.Promotion.PromotionMapEntityUpdateDto;
import xjanua.backend.dto.Promotion.PromotionSummaryByShopDto;
import xjanua.backend.dto.Promotion.PromotionUpdateDto;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftCreateDto;
import xjanua.backend.dto.Promotion.PromotionGift.PromotionGiftUpdateDto;
import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleCreateDto;
import xjanua.backend.dto.Promotion.PromotionRule.PromotionRuleUpdateDto;
import xjanua.backend.dto.product.UpdateStatusForHangDto;
import xjanua.backend.mapper.PromotionMapper;
import xjanua.backend.model.Promotion;
import xjanua.backend.model.Shop;
import xjanua.backend.repository.PromotionRepo;
import xjanua.backend.service.RedisService;
import xjanua.backend.service.interfaces.ExternalJobService;
import xjanua.backend.util.CommonUtil;
import xjanua.backend.util.PaginationUtil;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepo promotionRepo;
    private final ShopService shopService;
    private final PromotionGiftService promotionGiftService;
    private final PromotionRuleService promotionRuleService;
    private final PromotionMapper promotionMapper;
    private final ExternalJobService externalJobService;
    private final RedisService redisService;

    public Promotion fetchById(String promotionId) {
        return promotionRepo.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.PROMOTION_NOT_FOUND_MESSAGE));
    }

    public PaginationDTO.Response fetchAllPromotionByShop(Specification<Promotion> specification, Pageable pageable) {
        Shop shop = shopService.fetchByUserLogin();

        Specification<Promotion> shopSpec = (root, query, cb) -> cb.and(
                cb.equal(root.get("shop").get("id"), shop.getId()),
                cb.notEqual(root.get("status"), 3));

        Specification<Promotion> finalSpec = (specification == null) ? shopSpec : specification.and(shopSpec);

        PaginationDTO.Response response = new PaginationDTO.Response();
        Page<Promotion> promotions = this.promotionRepo.findAll(finalSpec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(promotions, pageable);

        List<PromotionSummaryByShopDto> promotionDTOs = promotions.getContent()
                .stream()
                .map(promotionMapper::toPromotionSummaryByShopDto)
                .collect(Collectors.toList());

        response.setInfo(info);
        response.setResponse(promotionDTOs);
        return response;
    }

    @Transactional
    public Promotion createPromotion(PromotionMapEntityCreateDto dto) {

        Shop shop = shopService.fetchByUserLogin();

        PromotionCreateDto promotionCreateDto = dto.getPromotionCreateDto();
        List<PromotionGiftCreateDto> promotionGifts = dto.getPromotionGifts();
        List<PromotionRuleCreateDto> promotionRules = dto.getPromotionRules();

        if (!CommonUtil.isEndTimeAfterStartTime(promotionCreateDto.getStartTime(), promotionCreateDto.getEndTime())) {
            throw new BadRequestException(ResponseConstants.END_TIME_BEFORE_START_TIME);
        }

        Promotion promotion = Promotion.builder()
                .name(promotionCreateDto.getName())
                .status(promotionCreateDto.getStatus())
                .startTime(promotionCreateDto.getStartTime())
                .endTime(promotionCreateDto.getEndTime())
                .totalUsageLimit(promotionCreateDto.getTotalUsageLimit())
                .shop(shop)
                .build();

        promotionRepo.save(promotion);

        promotionGiftService.createPromotionGifts(promotion, promotionGifts, shop.getId());
        promotionRuleService.createPromotionRules(promotion, promotionRules, shop.getId());

        schedulePromotionJobs(promotion.getId(), promotionCreateDto.getStartTime(), promotionCreateDto.getEndTime());

        List<String> productIds = promotionRules.stream().map(PromotionRuleCreateDto::getProductId).collect(Collectors.toList());
        
        String key = "peshop:promotion_product_ids";

        List<String> oldList = redisService.getList(key, String.class);

        List<String> merged = new ArrayList<>();

        if (oldList != null) {
            merged.addAll(oldList);
        }

        merged.addAll(productIds);

        merged = merged.stream().distinct().toList();

        redisService.setList(key, merged, 60 * 60 * 24 * 30);
        return promotion;
    }

    @Transactional
    public Promotion updatePromotion(String promotionId, PromotionMapEntityUpdateDto dto) {
        String shopId = shopService.fetchByUserLogin().getId();
        PromotionUpdateDto promotionUpdateDto = dto.getPromotionUpdateDto();
        List<PromotionGiftUpdateDto> promotionGifts = dto.getPromotionGifts();
        List<PromotionRuleUpdateDto> promotionRules = dto.getPromotionRules();

        Promotion promotion = fetchById(promotionId);
        checkPermissionOnPromotion(promotion, shopId);
        checkPromotionDeleted(promotion);
        if (!CommonUtil.isEndTimeAfterStartTime(promotionUpdateDto.getStartTime(), promotionUpdateDto.getEndTime())) {
            throw new BadRequestException(ResponseConstants.END_TIME_BEFORE_START_TIME);
        }

        if (promotion.getStatus() == 2) {
            if (promotionUpdateDto.getEndTime().isAfter(Instant.now())) {
                promotion.setStatus(1);
            }
        }

        promotion.setName(promotionUpdateDto.getName());
        promotion.setTotalUsageLimit(promotionUpdateDto.getTotalUsageLimit());

        if (promotionUpdateDto.getStartTime() != promotion.getStartTime()
            || promotionUpdateDto.getEndTime() != promotion.getEndTime()) {
        externalJobService.callDeleteJob("StartPromotionJob" + promotion.getId());
        externalJobService.callDeleteJob("EndPromotionJob" + promotion.getId());
        schedulePromotionJobs(promotion.getId(), promotionUpdateDto.getStartTime(), promotionUpdateDto.getEndTime());
    }

        promotionRepo.save(promotion);

        promotionGiftService.updatePromotionGifts(promotionGifts, shopId);
        promotionRuleService.updatePromotionRules(promotionRules, shopId);

        return promotion;
    }

    public Promotion addPromotionGift(String promotionId, List<PromotionGiftCreateDto> dto) {
        String shopId = shopService.fetchByUserLogin().getId();
        Promotion promotion = fetchById(promotionId);
        checkPermissionOnPromotion(promotion, shopId);
        checkPromotionDeleted(promotion);
        promotionGiftService.createPromotionGifts(promotion, dto, shopId);
        return promotion;
    }

    public Promotion addPromotionRule(String promotionId, List<PromotionRuleCreateDto> dto) {
        String shopId = shopService.fetchByUserLogin().getId();
        Promotion promotion = fetchById(promotionId);
        checkPermissionOnPromotion(promotion, shopId);
        checkPromotionDeleted(promotion);
        promotionRuleService.createPromotionRules(promotion, dto, shopId);
        return promotion;
    }

    public void deletePromotionGifts(List<String> promotionGiftIds) {
        String shopId = shopService.fetchByUserLogin().getId();
        promotionGiftService.deletePromotionGifts(promotionGiftIds, shopId);
    }

    public void deletePromotionRules(List<String> promotionRuleIds) {
        String shopId = shopService.fetchByUserLogin().getId();
        promotionRuleService.deletePromotionRules(promotionRuleIds, shopId);
    }

    public void updatePromotionStatus(String promotionId, Integer status, boolean isDeleted) {
        String shopId = shopService.fetchByUserLogin().getId();
        Promotion promotion = fetchById(promotionId);
        checkPermissionOnPromotion(promotion, shopId);
        checkPromotionDeleted(promotion);

        if (isDeleted) {
            promotion.setStatus(3);
        } else {
            if (status == null || (status != 0 && status != 1)) {
                throw new BadRequestException("Invalid status value. Allowed values: 0, 1");
            }
            promotion.setStatus(status);
        }
        promotionRepo.save(promotion);
    }

    public void checkPermissionOnPromotion(Promotion promotion, String shopId) {
        if (!promotion.getShop().getId().equals(shopId)) {
            throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
        }
    }

    public void checkPromotionDeleted(Promotion promotion) {
        if (promotion.getStatus() == 3) {
            throw new BadRequestException("Promotion is Deleted");
        }
    }

    @Async
    public void schedulePromotionJobs(String promotionId, Instant startTime, Instant endTime) {

        UpdateStatusForHangDto startDto = new UpdateStatusForHangDto();
        startDto.setId(promotionId);
        startDto.setStatus(1);

        String jsonPayloadStart = CommonUtil.toJson(startDto);

        UpdateStatusForHangDto endDto = new UpdateStatusForHangDto();
        endDto.setId(promotionId);
        endDto.setStatus(2);

        String jsonPayloadEnd = CommonUtil.toJson(endDto);

        externalJobService.callSetJob(
                "StartPromotionJob" + promotionId,
                "/shop/promotion/status/hang-fire",
                jsonPayloadStart,
                startTime);

        externalJobService.callSetJob(
                "EndPromotionJob" + promotionId,
                "/shop/promotion/status/hang-fire",
                jsonPayloadEnd,
                endTime);
    }

    public void updateStatusPromotionByHangFire(UpdateStatusForHangDto updateStatusForHangDto) {
        Promotion promotion = fetchById(updateStatusForHangDto.getId());
        promotion.setStatus(updateStatusForHangDto.getStatus());
        if(updateStatusForHangDto.getStatus() == 2) {
            String key = "peshop:promotion_product_ids";
            List<String> productIds = promotion.getRules().stream().map(promotionRule -> promotionRule.getProduct().getId()).collect(Collectors.toList());
            redisService.setList(key, productIds, 60 * 60 * 24 * 30);
        }
        promotionRepo.save(promotion);
    }
}