package xjanua.backend.service.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import xjanua.backend.dto.propertyProduct.PropertyProductCreateDto;
import xjanua.backend.dto.propertyProduct.PropertyProductUpdateDto;
import xjanua.backend.model.PropertyProduct;
import xjanua.backend.repository.PropertyProductRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class PropertyProductService {
    private final PropertyProductRepo propertyProductRepo;

    public PropertyProductService(PropertyProductRepo propertyProductRepo) {
        this.propertyProductRepo = propertyProductRepo;
    }

    public PropertyProduct fetchById(String propertyProductId) {
        return propertyProductRepo.findById(propertyProductId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.PROPERTY_PRODUCT_NOT_FOUND_MESSAGE));
    }

    public List<PropertyProduct> fetchAll() {
        return propertyProductRepo.findAll();
    }

    public List<PropertyProduct> createPropertyProduct(List<PropertyProductCreateDto> requests) {
        return propertyProductRepo.saveAll(requests.stream()
                .map(request -> PropertyProduct.builder()
                        .name(request.getName())
                        .build())
                .collect(Collectors.toList()));
    }

    public List<PropertyProduct> updatePropertyProduct(List<PropertyProductUpdateDto> requests) {
        return propertyProductRepo.saveAll(requests.stream()
                .map(request -> {
                    PropertyProduct propertyProduct = fetchById(request.getId());
                    propertyProduct.setName(request.getName());
                    return propertyProduct;
                })
                .collect(Collectors.toList()));
    }
}
