package xjanua.backend.service.shop;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.product.UpdateStatusForHangDto;
import xjanua.backend.dto.voucher.shop.VoucherShopCreateDto;
import xjanua.backend.dto.voucher.shop.VoucherShopUpdateDto;
import xjanua.backend.dto.voucher.shop.VoucherSummaryDto;
import xjanua.backend.mapper.VoucherShopMapper;
import xjanua.backend.model.Shop;
import xjanua.backend.model.VoucherShop;
import xjanua.backend.repository.VoucherShopRepo;
import xjanua.backend.service.interfaces.ExternalJobService;
import xjanua.backend.util.CommonUtil;
import xjanua.backend.util.PaginationUtil;
import xjanua.backend.util.Enum.VoucherShopEnum;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class VoucherShopService {

    private final VoucherShopRepo voucherShopRepo;
    private final ShopService shopService;
    private final VoucherShopMapper voucherShopMapper;
    private final ExternalJobService externalJobService;

    public VoucherShop fetchById(String voucherShopId) {
        return voucherShopRepo.findById(voucherShopId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.VOUCHER_NOT_FOUND_MESSAGE));
    }

    public PaginationDTO.Response fetchAllVoucherShopByShop(Specification<VoucherShop> specification,
            Pageable pageable) {
        String shopId = shopService.fetchByUserLogin().getId();

        Specification<VoucherShop> shopSpec = (root, query, cb) -> cb.and(
                cb.equal(root.get("shop").get("id"), shopId));

        Specification<VoucherShop> finalSpec = (specification == null) ? shopSpec : specification.and(shopSpec);

        PaginationDTO.Response response = new PaginationDTO.Response();
        Page<VoucherShop> voucherShops = this.voucherShopRepo.findAll(finalSpec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(voucherShops, pageable);

        List<VoucherSummaryDto> voucherShopDTOs = voucherShops.getContent()
                .stream()
                .map(voucherShopMapper::toVoucherSummaryDto)
                .collect(Collectors.toList());

        response.setInfo(info);
        response.setResponse(voucherShopDTOs);
        return response;
    }

    public VoucherShop createVoucherShop(VoucherShopCreateDto request) {
        Shop shop = shopService.fetchByUserLogin();

        if (voucherShopRepo.existsByCodeAndShopIdAndStatusNot(request.getCode(), shop.getId(),
                VoucherShopEnum.status.ENDED.getValue())) {
            throw new BadRequestException(ResponseConstants.VOUCHER_CODE_ALREADY_EXISTS);
        }

        if (!CommonUtil.isEndTimeAfterStartTime(request.getStartTime(), request.getEndTime())) {
            throw new BadRequestException(ResponseConstants.END_TIME_BEFORE_START_TIME);
        }

        int type = VoucherShopEnum.type.fromValue(request.getType()).getValue();
        int status = VoucherShopEnum.status.INACTIVE.getValue();

        VoucherShop voucherShop = new VoucherShop();

        if (type == VoucherShopEnum.type.FIXED_AMOUNT.getValue()) {
            voucherShop.setMaxDiscountAmount(null);
        } else {
            if (request.getDiscountValue() > 100) {
                throw new BadRequestException(ResponseConstants.VOUCHER_DISCOUNT_VALUE_CANNOT_GREATER_THAN_100);
            }
            voucherShop.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }

        voucherShop.setName(request.getName());
        voucherShop.setCode(request.getCode());
        voucherShop.setType(type);
        voucherShop.setDiscountValue(request.getDiscountValue());
        voucherShop.setMinimumOrderValue(request.getMinimumOrderValue());
        voucherShop.setQuantity(request.getQuantity());
        voucherShop.setQuantityUsed(0);
        voucherShop.setLimitForUser(request.getLimitForUser());
        voucherShop.setStartTime(request.getStartTime());
        voucherShop.setEndTime(request.getEndTime());
        voucherShop.setShop(shop);
        voucherShop.setStatus(status);

        VoucherShop savedVoucherShop = voucherShopRepo.save(voucherShop);

        scheduleVoucherShopJobs(savedVoucherShop.getId(), savedVoucherShop.getStartTime(),
                savedVoucherShop.getEndTime());

        return savedVoucherShop;
    }

    @Transactional
    public VoucherShop updateVoucherShop(String voucherShopId, VoucherShopUpdateDto request) {
        String shopId = shopService.fetchByUserLogin().getId();
        VoucherShop voucherShop = fetchById(voucherShopId);

        checkPermissionOnVoucherShop(voucherShop, shopId);

        if (voucherShop.getStatus() == VoucherShopEnum.status.INACTIVE.getValue()) {
            if (!CommonUtil.isEndTimeAfterStartTime(request.getStartTime(), request.getEndTime())) {
                throw new BadRequestException(ResponseConstants.END_TIME_BEFORE_START_TIME);
            }
            voucherShop.setName(request.getName());
            voucherShop.setDiscountValue(request.getDiscountValue());
            voucherShop.setMinimumOrderValue(request.getMinimumOrderValue());
            voucherShop.setQuantity(request.getQuantity());
            if (request.getStartTime() != voucherShop.getStartTime()
                    || request.getEndTime() != voucherShop.getEndTime()) {
                externalJobService.callDeleteJob("StartVoucherShopJob" + voucherShop.getId());
                externalJobService.callDeleteJob("EndVoucherShopJob" + voucherShop.getId());
                scheduleVoucherShopJobs(voucherShop.getId(), request.getStartTime(), request.getEndTime());
            }
        }

        if (voucherShop.getStatus() == VoucherShopEnum.status.ACTIVE.getValue()) {
            voucherShop.setName(request.getName());
            voucherShop.setQuantity(request.getQuantity());
        }

        if (voucherShop.getStatus() == VoucherShopEnum.status.ENDED.getValue()) {
            throw new BadRequestException(ResponseConstants.VOUCHER_SHOP_NOT_ACTIVE);
        }

        return voucherShopRepo.save(voucherShop);
    }

    @Transactional
    public void endVoucherShop(List<String> voucherShopIds) {
        String shopId = shopService.fetchByUserLogin().getId();
        List<VoucherShop> voucherShops = voucherShopRepo.findAllById(voucherShopIds);
        if (voucherShops.size() != voucherShopIds.size()) {
            throw new BadRequestException(ResponseConstants.ERROR_MESSAGE);
        }
        for (VoucherShop voucherShop : voucherShops) {
            checkPermissionOnVoucherShop(voucherShop, shopId);
            voucherShop.setStatus(VoucherShopEnum.status.ENDED.getValue());
        }
        voucherShopRepo.saveAll(voucherShops);
    }

    @Transactional
    public void deleteVoucherShops(List<String> voucherShopIds) {
        String shopId = shopService.fetchByUserLogin().getId();
        List<VoucherShop> voucherShops = voucherShopRepo.findAllById(voucherShopIds);
        if (voucherShops.size() != voucherShopIds.size()) {
            throw new BadRequestException(ResponseConstants.ERROR_MESSAGE);
        }
        for (VoucherShop voucherShop : voucherShops) {
            checkPermissionOnVoucherShop(voucherShop, shopId);
            if (voucherShop.getStatus() == VoucherShopEnum.status.ACTIVE.getValue()
                    || voucherShop.getStatus() == VoucherShopEnum.status.ENDED.getValue()) {
                throw new BadRequestException(ResponseConstants.ERROR_MESSAGE);
            }
        }
        try {
            voucherShopRepo.deleteAll(voucherShops);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(ResponseConstants.VOUCHER_SHOP_IN_USE);
        }
    }

    public void updateStatusVoucherShopByHangFire(UpdateStatusForHangDto updateStatusForHangDto) {
        VoucherShop voucherShop = fetchById(updateStatusForHangDto.getId());
        voucherShop.setStatus(updateStatusForHangDto.getStatus());
        voucherShopRepo.save(voucherShop);
    }

    public void checkPermissionOnVoucherShop(VoucherShop voucherShop, String shopId) {
        if (!voucherShop.getShop().getId().equals(shopId)) {
            throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
        }
    }

    @Async
    public void scheduleVoucherShopJobs(String voucherShopId, Instant startTime, Instant endTime) {

        UpdateStatusForHangDto startDto = new UpdateStatusForHangDto();
        startDto.setId(voucherShopId);
        startDto.setStatus(VoucherShopEnum.status.ACTIVE.getValue());

        String jsonPayloadStart = CommonUtil.toJson(startDto);

        UpdateStatusForHangDto endDto = new UpdateStatusForHangDto();
        endDto.setId(voucherShopId);
        endDto.setStatus(VoucherShopEnum.status.ENDED.getValue());

        String jsonPayloadEnd = CommonUtil.toJson(endDto);

        externalJobService.callSetJob(
                "StartVoucherShopJob" + voucherShopId,
                "/voucher-shop/status/hang-fire",
                jsonPayloadStart,
                startTime);

        externalJobService.callSetJob(
                "EndVoucherShopJob" + voucherShopId,
                "/voucher-shop/status/hang-fire",
                jsonPayloadEnd,
                endTime);
    }
}