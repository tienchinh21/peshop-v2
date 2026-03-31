package xjanua.backend.service.shop;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.order.VoucherOrderResponseDto;
import xjanua.backend.mapper.OrderMapper;
import xjanua.backend.model.OrderVoucher;
import xjanua.backend.model.VoucherShop;
import xjanua.backend.repository.OrderVoucherRepo;
import xjanua.backend.util.PaginationUtil;

@Service
@RequiredArgsConstructor
public class OrderVoucherService {
    private final OrderVoucherRepo orderVoucherRepo;
    private final OrderMapper orderMapper;
    private final VoucherShopService voucherShopService;
    private final ShopService shopService;

    public PaginationDTO.Response fetchAllByVoucherShopId(String voucherShopId, Pageable pageable) {
        String shopId = shopService.fetchByUserLogin().getId();
        VoucherShop voucherShop = voucherShopService.fetchById(voucherShopId);
        voucherShopService.checkPermissionOnVoucherShop(voucherShop, shopId);

        Page<OrderVoucher> pageOv = orderVoucherRepo.findByVoucherShopId(voucherShopId, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(pageOv, pageable);

        List<VoucherOrderResponseDto> orderDTOs = pageOv.getContent()
                .stream()
                .map(ov -> orderMapper.toVoucherOrderResponseDto(ov.getOrder()))
                .toList();

        PaginationDTO.Response response = new PaginationDTO.Response();
        response.setInfo(info);
        response.setResponse(orderDTOs);

        return response;
    }
}