package xjanua.backend.service.shop;

import java.util.List;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import xjanua.backend.dto.propertyValue.PropertyValueCreateDto;
import xjanua.backend.dto.propertyValue.PropertyValueUpdateDto;
import xjanua.backend.model.PropertyProduct;
import xjanua.backend.model.PropertyValue;
import xjanua.backend.repository.PropertyValueRepo;
import xjanua.backend.service.admin.PropertyProductService;
import xjanua.backend.service.interfaces.StorageService;
import xjanua.backend.util.FileUtil;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class PropertyValueService {
    private final PropertyValueRepo propertyValueRepo;
    private final PropertyProductService propertyProductService;
    private final StorageService storageService;
    private final ShopService shopService;

    public PropertyValueService(PropertyValueRepo propertyValueRepo, PropertyProductService propertyProductService,
            StorageService storageService, ShopService shopService) {
        this.shopService = shopService;
        this.propertyValueRepo = propertyValueRepo;
        this.propertyProductService = propertyProductService;
        this.storageService = storageService;
    }

    public PropertyValue fetchById(String propertyValueId) {
        return propertyValueRepo.findById(propertyValueId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.PROPERTY_VALUE_NOT_FOUND_MESSAGE));
    }

    public PropertyProduct fetchPropertyProduct(PropertyValue propertyValue) {
        return propertyValue.getPropertyProduct();
    }

    // private PropertyValue createPropertyValueNoSave(PropertyValueCreateDto
    // request) {
    // String newUrl = null;

    // if (request.getUrlImage() != null) {
    // String fileName = FileUtil.fetchFileName(request.getUrlImage());

    // if (!storageService.checkFileExists(List.of(fileName), "temp")) {
    // throw new BadRequestException("Ảnh không tồn tại: " + fileName);
    // }

    // newUrl = storageService.moveFile(fileName, "temp", "images/propertyValue");
    // }

    // return PropertyValue.builder()
    // .value(request.getValue())
    // .propertyProduct(propertyProductService.fetchById(request.getPropertyProductId()))
    // .level(request.getLevel())
    // .imgUrl(newUrl)
    // .code(request.getCode())
    // .build();
    // }

    private PropertyValue createPropertyValueNoSave(PropertyValueCreateDto request) {
        return PropertyValue.builder()
                .value(request.getValue())
                .propertyProduct(propertyProductService.fetchById(request.getPropertyProductId()))
                .level(request.getLevel())
                .imgUrl(request.getUrlImage())
                .code(request.getCode())
                .build();
    }

    @Transactional
    public List<PropertyValue> createListPropertyValue(List<PropertyValueCreateDto> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException("Property values are required");
        }
        List<PropertyValue> entities = requests.stream()
                .map(this::createPropertyValueNoSave)
                .toList();

        return propertyValueRepo.saveAll(entities);
    }

    public List<PropertyValue> updatePropertyValues(List<PropertyValueUpdateDto> requests) {
        String shopId = shopService.fetchByUserLogin().getId();

        return propertyValueRepo.saveAll(
                requests.stream()
                        .map(req -> updateSinglePropertyValue(req, shopId))
                        .toList());
    }

    private PropertyValue updateSinglePropertyValue(PropertyValueUpdateDto request, String shopId) {
        PropertyValue propertyValue = fetchById(request.getPropertyValueId());
        checkPermissionOnPropertyValue(propertyValue, shopId);

        String requestImgUrl = request.getUrlImage();
        String currentImgUrl = propertyValue.getImgUrl();
        int level = propertyValue.getLevel();

        if (level == 1) {
            if (requestImgUrl != null) {
                throw new BadRequestException("Cannot update image for level 1 property value");
            }
            propertyValue.setValue(request.getValue());
            return propertyValue;
        }

        if (level == 0) {
            if (requestImgUrl != null && !Objects.equals(requestImgUrl, currentImgUrl)) {
                String fileName = FileUtil.fetchFileName(requestImgUrl);

                if (!storageService.checkFileExist(fileName, "temp")) {
                    throw new BadRequestException("Image not found: " + fileName);
                }

                String newUrl = storageService.moveFile(fileName, "temp", "images/propertyValue");
                propertyValue.setImgUrl(newUrl);
            }
            propertyValue.setValue(request.getValue());
            return propertyValue;
        }

        throw new BadRequestException("Unsupported property value level: " + level);
    }

    public void checkPermissionOnPropertyValue(PropertyValue propertyValue, String shopId) {
        if (!propertyValue.getVariantValues().get(0).getVariant().getProduct().getShop().getId().equals(shopId)) {
            throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
        }
    }
}