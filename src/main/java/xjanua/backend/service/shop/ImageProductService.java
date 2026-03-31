package xjanua.backend.service.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import xjanua.backend.dto.image.ImagePrdCreateDto;
import xjanua.backend.dto.image.ImagePrdUpdateDto;
import xjanua.backend.model.ImageProduct;
import xjanua.backend.model.Product;
import xjanua.backend.repository.ImageProductRepo;
import xjanua.backend.service.interfaces.StorageService;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class ImageProductService {

    private final ImageProductRepo imageProductRepo;
    private final StorageService storageService;

    public ImageProductService(ImageProductRepo imageProductRepo, StorageService storagerService,
            ShopService shopService) {
        this.imageProductRepo = imageProductRepo;
        this.storageService = storagerService;
    }

    public ImageProduct fetchById(String imageProductId) {
        return imageProductRepo.findById(imageProductId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.IMAGE_PRODUCT_NOT_FOUND_MESSAGE));
    }

    // public List<ImageProduct> createImageProduct(List<ImagePrdCreateDto>
    // requests, Product product) {

    // List<ImagePrdCreateDto> sortedRequests = requests.stream()
    // .sorted(Comparator.comparing(ImagePrdCreateDto::getSortOrder))
    // .collect(Collectors.toList());

    // List<String> fileNames = FileUtil.fetchFileNames(
    // sortedRequests.stream()
    // .map(ImagePrdCreateDto::getUrlImage)
    // .collect(Collectors.toList()));

    // if (!storageService.checkFileExists(fileNames, "temp")) {
    // throw new BadRequestException("Ảnh không tồn tại");
    // }

    // List<String> newUrls = storageService.moveFiles(fileNames, "temp",
    // "images/product");

    // List<ImageProduct> images = IntStream.range(0, sortedRequests.size())
    // .mapToObj(i -> createSingleImageProduct(sortedRequests.get(i),
    // newUrls.get(i), product))
    // .collect(Collectors.toList());

    // return imageProductRepo.saveAll(images);
    // }

    // private ImageProduct createSingleImageProduct(ImagePrdCreateDto dto, String
    // movedUrl, Product product) {
    // return ImageProduct.builder()
    // .url(movedUrl)
    // .sortOrder(dto.getSortOrder())
    // .product(product)
    // .build();
    // }

    public List<ImageProduct> createListImageProduct(List<ImagePrdCreateDto> requests, Product product) {

        List<ImagePrdCreateDto> sortedRequests = requests.stream()
                .sorted(Comparator.comparing(ImagePrdCreateDto::getSortOrder))
                .collect(Collectors.toList());

        List<ImageProduct> images = IntStream.range(0, sortedRequests.size())
                .mapToObj(i -> {
                    return ImageProduct.builder()
                            .url(sortedRequests.get(i).getUrlImage())
                            .sortOrder(i)
                            .product(product)
                            .build();
                })
                .collect(Collectors.toList());

        return imageProductRepo.saveAll(images);
    }

    public void updateImageProduct(List<ImagePrdUpdateDto> imagesProduct, Product product,
            String shopId) {
        // Lấy tất cả images hiện tại của product
        List<ImageProduct> existingImages = imageProductRepo.findByProductId(product.getId());

        // Check permission
        for (ImageProduct img : existingImages) {
            checkPermissionOnImageProduct(img, shopId);
        }

        // Lấy danh sách id từ request
        List<String> requestIds = imagesProduct.stream()
                .map(ImagePrdUpdateDto::getId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toList());

        // Tìm images cần xóa (id cũ không có trong list gửi lên)
        List<ImageProduct> imagesToDelete = existingImages.stream()
                .filter(img -> !requestIds.contains(img.getId()))
                .collect(Collectors.toList());

        // Xóa images không còn trong list
        if (!imagesToDelete.isEmpty()) {
            imageProductRepo.deleteAll(imagesToDelete);
        }

        // Xử lý update và create
        List<ImageProduct> imagesToSave = new ArrayList<>();

        for (int i = 0; i < imagesProduct.size(); i++) {
            ImagePrdUpdateDto dto = imagesProduct.get(i);

            if (dto.getId() != null && !dto.getId().isEmpty()) {
                // Có id -> update
                ImageProduct existingImage = existingImages.stream()
                        .filter(img -> img.getId().equals(dto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + dto.getId()));

                existingImage.setUrl(dto.getUrlImage());
                existingImage.setSortOrder(i);
                imagesToSave.add(existingImage);
            } else {
                // Không có id -> tạo mới
                ImageProduct newImage = ImageProduct.builder()
                        .url(dto.getUrlImage())
                        .sortOrder(i)
                        .product(product)
                        .build();
                imagesToSave.add(newImage);
            }
        }

        // Lưu tất cả
        imageProductRepo.saveAll(imagesToSave);
    }

    public void deleteImageProduct(List<String> imageProductIds, String shopId) {
        List<ImageProduct> imageProducts = imageProductRepo.findAllById(imageProductIds);
        for (ImageProduct imageProduct : imageProducts) {
            checkPermissionOnImageProduct(imageProduct, shopId);
        }
        imageProductRepo.deleteAll(imageProducts);
    }

    public void checkPermissionOnImageProduct(ImageProduct imageProduct, String shopId) {
        if (!imageProduct.getProduct().getShop().getId().equals(shopId)) {
            throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
        }
    }
}