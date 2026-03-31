package xjanua.backend.service.shop;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import xjanua.backend.dto.product.information.ProductInformationCreateDto;
import xjanua.backend.dto.product.information.ProductInformationUpdateDto;
import xjanua.backend.model.Product;
import xjanua.backend.model.ProductInformation;
import xjanua.backend.repository.ProductInformationRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class ProductInformationService {

        private final ProductInformationRepo productInformationRepo;
        private final ShopService shopService;

        public ProductInformationService(ProductInformationRepo productInformationRepo, ShopService shopService) {
                this.productInformationRepo = productInformationRepo;
                this.shopService = shopService;
        }

        public ProductInformation fetchById(Integer productInformationId) {
                return productInformationRepo.findById(productInformationId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ResponseConstants.PRODUCT_INFORMATION_NOT_FOUND_MESSAGE));
        }

        private ProductInformation createProductInformationNoSave(ProductInformationCreateDto request,
                        Product product) {
                return ProductInformation.builder()
                                .name(request.getName())
                                .value(request.getValue())
                                .product(product)
                                .build();
        }

        private ProductInformation updateSingleProductInformation(ProductInformationUpdateDto req, String shopId) {
                ProductInformation productInformation = fetchById(req.getId());
                checkPermissionOnProductInformation(productInformation, shopId);
                productInformation.setValue(req.getValue());
                return productInformation;
        }

        public List<ProductInformation> createProductInformations(List<ProductInformationCreateDto> requestList,
                        Product product) {
                List<ProductInformation> entities = requestList.stream()
                                .map(req -> createProductInformationNoSave(req, product))
                                .collect(Collectors.toList());

                return productInformationRepo.saveAll(entities);
        }

        public List<ProductInformation> updateProductInformation(List<ProductInformationUpdateDto> requestList) {
                String shopId = shopService.fetchByUserLogin().getId();

                return productInformationRepo.saveAll(
                                requestList.stream().map(req -> updateSingleProductInformation(req, shopId))
                                                .collect(Collectors.toList()));
        }

        public void checkPermissionOnProductInformation(ProductInformation productInformation, String shopId) {
                if (!productInformation.getProduct().getShop().getId().equals(shopId)) {
                        throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
                }
        }
}
