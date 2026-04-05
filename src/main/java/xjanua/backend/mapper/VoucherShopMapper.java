package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.voucher.shop.VoucherShopDetailDto;
import xjanua.backend.dto.voucher.shop.VoucherSummaryDto;
import xjanua.backend.model.VoucherShop;

@Mapper(componentModel = "spring")
public interface VoucherShopMapper {
    VoucherSummaryDto toVoucherSummaryDto(VoucherShop voucherShop);

    VoucherShopDetailDto toVoucherShopDetailDto(VoucherShop voucherShop);
}