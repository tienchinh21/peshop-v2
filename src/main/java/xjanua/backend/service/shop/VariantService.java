package xjanua.backend.service.shop;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import xjanua.backend.dto.variant.VariantCreateDto;
import xjanua.backend.dto.variant.VariantUpdateDto;
import xjanua.backend.model.Product;
import xjanua.backend.model.PropertyValue;
import xjanua.backend.model.Variant;
import xjanua.backend.repository.VariantRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class VariantService {
    private final VariantRepo variantRepo;
    private final ShopService shopService;

    public VariantService(VariantRepo variantRepo, ShopService shopService) {
        this.variantRepo = variantRepo;
        this.shopService = shopService;
    }

    public List<Variant> saveAll(List<Variant> variants) {
        return variantRepo.saveAll(variants);
    }

    public Variant fetchById(Integer variantId) {
        return variantRepo.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.VARIANT_NOT_FOUND_MESSAGE));
    }

    public Variant createVariant(VariantCreateDto request, Product product) {
        return variantRepo.save(Variant.builder()
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .product(product)
                .status(request.getStatus())
                .build());
    }

    public List<Variant> updateVariant(List<VariantUpdateDto> requests) {
        String shopId = shopService.fetchByUserLogin().getId();

        return variantRepo.saveAll(
                requests.stream()
                        .map(req -> updateSingleVariant(req, shopId))
                        .collect(Collectors.toList()));
    }

    public List<String> fetchSortedPropertyValueNames(Integer variantId) {
        Variant variant = variantRepo.findDetailById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.VARIANT_NOT_FOUND_MESSAGE));

        return variant.getVariantValues()
                .stream()
                .map(vv -> vv.getPropertyValue())
                .sorted((a, b) -> {
                    Integer l1 = a.getLevel() == null ? 1 : a.getLevel();
                    Integer l2 = b.getLevel() == null ? 1 : b.getLevel();
                    return l1.compareTo(l2);
                })
                .map(PropertyValue::getValue)
                .collect(Collectors.toList());
    }

    private Variant updateSingleVariant(VariantUpdateDto request, String shopId) {
        Variant variant = fetchById(request.getVariantId());
        checkPermissionOnVariant(variant, shopId);

        variant.setPrice(request.getPrice());
        variant.setQuantity(request.getQuantity());
        variant.setStatus(request.getStatus());

        return variant;
    }

    public void checkPermissionOnVariant(Variant variant, String shopId) {
        if (!variant.getProduct().getShop().getId().equals(shopId)) {
            throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
        }
    }
}