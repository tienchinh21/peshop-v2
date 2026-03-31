package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.FlashSale.FlashSaleResponeDto;
import xjanua.backend.model.FlashSale;

@Mapper(componentModel = "spring", uses = { ProductMapper.class, FlashSaleProductMapper.class })
public interface FlashSaleMapper {
    FlashSaleResponeDto toFlashSaleResponeDto(FlashSale entity);
}