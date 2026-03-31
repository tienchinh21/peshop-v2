package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.FlashSale.FlashSaleProduct.FlashSaleProductResponseDto;
import xjanua.backend.model.FlashSaleProduct;

@Mapper(componentModel = "spring", uses = { ProductMapper.class, FlashSaleMapper.class })
public interface FlashSaleProductMapper {

    FlashSaleProductResponseDto toFlashSaleProductResponseDto(FlashSaleProduct entity);
}