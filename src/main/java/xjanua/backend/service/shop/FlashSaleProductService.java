package xjanua.backend.service.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.FlashSale.FlashSaleGroupDto;
import xjanua.backend.dto.FlashSale.FlashSaleResponeDto;
import xjanua.backend.dto.FlashSale.FlashSaleProduct.FlashSaleProductCreateDto;
import xjanua.backend.dto.FlashSale.FlashSaleProduct.FlashSaleProductResponseDto;
import xjanua.backend.dto.product.ProductDtoForBot;
import xjanua.backend.mapper.FlashSaleProductMapper;
import xjanua.backend.model.FlashSale;
import xjanua.backend.model.FlashSaleProduct;
import xjanua.backend.model.Product;
import xjanua.backend.repository.FlashSaleProductRepo;
import xjanua.backend.service.RedisService;
import xjanua.backend.util.Enum.FlashSaleEnum;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class FlashSaleProductService {
    private final FlashSaleProductRepo flashSaleProductRepo;
    private final FlashSaleService flashSaleService;
    private final ProductService productService;
    private final ShopService shopService;
    private final FlashSaleProductMapper flashSaleProductMapper;
    private final RedisService redisService;

    private FlashSaleProduct buildFlashSaleProduct(
            FlashSale flashSale,
            String shopId,
            FlashSaleProductCreateDto request) {
        Product product = productService.fetchById(request.getProductId());
        productService.checkPermissionOnProduct(product, shopId);

        return FlashSaleProduct.builder()
                .flashSale(flashSale)
                .product(product)
                .percentDecrease(request.getPercentDecrease())
                .quantity(request.getQuantity())
                .orderLimit(request.getOrderLimit())
                .build();
    }

    public void joinFlashSales(String flashSaleId, List<FlashSaleProductCreateDto> requests) {
        String shopId = shopService.fetchByUserLogin().getId();
        FlashSale flashSale = flashSaleService.findById(flashSaleId);
        List<FlashSaleProduct> flashSaleProducts = new ArrayList<>();

        if (flashSale.getStatus() == FlashSaleEnum.status.INACTIVE.getValue()) {

            flashSaleProducts = requests.stream()
                    .map(request -> buildFlashSaleProduct(flashSale, shopId, request))
                    .toList();

            flashSaleProductRepo.saveAll(flashSaleProducts);
        } else {
            throw new BadRequestException(ResponseConstants.FLASH_SALE_STATUS_INVALID);
        }

        String key = "peshop:flash_sale_product_ids";
        List<String> productIds = flashSaleProducts.stream().map(flashSaleProduct -> flashSaleProduct.getProduct().getId()).collect(Collectors.toList());
        redisService.setList(key, productIds, 60 * 60 * 24 * 30);
    }

    public List<FlashSaleGroupDto> getFlashSaleProducts() {
        String shopId = shopService.fetchByUserLogin().getId();

        List<FlashSaleProduct> flashSaleProducts = flashSaleProductRepo
                .findAllByShopIdWithRelations(shopId);

        List<FlashSaleProductResponseDto> dtos = flashSaleProducts.stream()
                .map(flashSaleProductMapper::toFlashSaleProductResponseDto)
                .collect(Collectors.toList());

        Map<FlashSaleResponeDto, List<ProductDtoForBot>> grouped = dtos.stream()
                .collect(Collectors.groupingBy(
                        FlashSaleProductResponseDto::getFlashSale,
                        Collectors.mapping(
                                FlashSaleProductResponseDto::getProduct,
                                Collectors.toList())));

        List<FlashSaleGroupDto> result = grouped.entrySet().stream()
                .map(entry -> {
                    FlashSaleGroupDto dto = new FlashSaleGroupDto();
                    dto.setFlashSale(entry.getKey());
                    dto.setProducts(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());

        return result;
    }
}