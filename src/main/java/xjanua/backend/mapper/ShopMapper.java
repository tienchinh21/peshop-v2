package xjanua.backend.mapper;

import org.mapstruct.Mapper;

import xjanua.backend.dto.shop.ShopDetailDtoByMe;
import xjanua.backend.model.Shop;

@Mapper(componentModel = "spring")
public interface ShopMapper {
    ShopDetailDtoByMe toShopDetailDtoByMe(Shop shop);
}
