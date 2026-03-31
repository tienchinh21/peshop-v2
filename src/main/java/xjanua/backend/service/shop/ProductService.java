package xjanua.backend.service.shop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.image.ImagePrdUpdateDto;
import xjanua.backend.dto.product.ProductCreateDto;
import xjanua.backend.dto.product.ProductDtoForBot;
import xjanua.backend.dto.product.ProductMapEntityCreateDto;
import xjanua.backend.dto.product.ProductMapEntityUpdateDto;
import xjanua.backend.dto.product.ProductSummaryByShopDto;
import xjanua.backend.dto.product.ProductUpdateDto;
import xjanua.backend.dto.product.information.ProductInformationUpdateDto;
import xjanua.backend.dto.propertyValue.PropertyValueCreateDto;
import xjanua.backend.dto.propertyValue.PropertyValueUpdateDto;
import xjanua.backend.dto.variant.VariantCreateDtoMapWithKeys;
import xjanua.backend.dto.variant.VariantUpdateDto;
import xjanua.backend.mapper.ProductMapper;
import xjanua.backend.model.ImageProduct;
import xjanua.backend.model.Product;
import xjanua.backend.model.Shop;
import xjanua.backend.model.Variant;
import xjanua.backend.repository.ProductRepo;
import xjanua.backend.service.RedisService;
import xjanua.backend.service.admin.category.CategoryChildService;
import xjanua.backend.service.interfaces.ExternalJobService;
import xjanua.backend.util.CommonUtil;
import xjanua.backend.util.PaginationUtil;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductService {

    // ==========================================================
    // Dependencies
    // ==========================================================
    private final ProductRepo productRepo;
    private final CategoryChildService categoryChildService;
    private final ShopService shopService;
    private final VariantProcessingService variantProcessingService;
    private final PropertyValueService propertyValueService;
    private final VariantService variantService;
    private final ProductInformationService productInformationService;
    private final ProductMapper productMapper;
    private final ImageProductService imageProductService;
    private final RedisService redisService;
    private final ExternalJobService externalJobService;

    // ==========================================================
    // Basic methods
    // ==========================================================
    public Product fetchById(String productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.PRODUCT_NOT_FOUND_MESSAGE));
    }

    public Product fetchByIdByShop(String productId) {
        String shopId = shopService.fetchByUserLogin().getId();
        Product product = productRepo.findByIdWithDetail(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.PRODUCT_NOT_FOUND_MESSAGE));
        checkPermissionOnProduct(product, shopId);
        checkProductDeleted(product);
        return product;
    }

    public PaginationDTO.Response fetchAllProductByShop(Specification<Product> specification, Pageable pageable) {
        String shopId = shopService.fetchByUserLogin().getId();

        /*
         * Không lọc theo status = 2
         */
        Specification<Product> shopSpec = (root, query, cb) -> cb.and(
                cb.equal(root.get("shop").get("id"), shopId),
                cb.notEqual(root.get("status"), 2));

        Specification<Product> finalSpec = (specification == null) ? shopSpec : specification.and(shopSpec);

        PaginationDTO.Response response = new PaginationDTO.Response();
        Page<Product> products = this.productRepo.findAll(finalSpec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(products, pageable);

        List<ProductSummaryByShopDto> productDTOs = products.getContent()
                .stream()
                .map(productMapper::toProductSummaryByShopDto)
                .collect(Collectors.toList());

        response.setInfo(info);
        response.setResponse(productDTOs);
        return response;
    }

    public void saveProduct(Product product) {
        productRepo.save(product);
    }

    // ==========================================================
    // Create / Update methods
    // ==========================================================
    @Transactional
    public Product createProduct(ProductMapEntityCreateDto dto) throws IOException {
        Shop shop = shopService.fetchByUserLogin();

        ProductCreateDto request = dto.getProduct();
        List<PropertyValueCreateDto> propertyValues = dto.getPropertyValues();
        List<VariantCreateDtoMapWithKeys> variants = dto.getVariants();

        variantProcessingService.validateVariantInput(request.getClassify(), propertyValues, variants);

        Product product = productRepo.save(Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryChild(categoryChildService.fetchById(request.getCategoryChildId()))
                .category(categoryChildService.fetchCategoryByCategoryChildId(request.getCategoryChildId()))
                .shop(shop)
                .slug(CommonUtil.toSlug(request.getName()))
                .status(0)
                .weight(request.getWeight())
                .height(request.getHeight())
                .length(request.getLength())
                .width(request.getWidth())
                .classify(request.getClassify())
                .status(4)
                .build());

        List<ImageProduct> savedImages = imageProductService.createListImageProduct(dto.getImagesProduct(), product);
        product.setImgMain(savedImages.get(0).getUrl());
        productInformationService.createProductInformations(dto.getProductInformations(), product);

        variantProcessingService.processVariants(propertyValues, variants, product, request.getClassify());

        BigDecimal lowestPrice = getLowestPrice(product);
        product.setPrice(lowestPrice);
        saveProduct(product);

        pushPendingProductAsync(product);

        return product;
    }

    @Transactional
    public void updateProduct(String productId, ProductMapEntityUpdateDto dto) throws IOException {
        String shopId = shopService.fetchByUserLogin().getId();
        Product product = fetchById(productId);
        checkPermissionOnProduct(product, shopId);
        checkProductDeleted(product);

        ProductUpdateDto request = dto.getProduct();
        List<ImagePrdUpdateDto> imagesProduct = dto.getImagesProduct();
        List<ProductInformationUpdateDto> productInformations = dto.getProductInformations();
        List<PropertyValueUpdateDto> propertyValues = dto.getPropertyValues();
        List<VariantUpdateDto> variants = dto.getVariants();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        // Vì status có xử lí logic nên phải gọi hàm riêng để tránh lỗi
        updateProductStatus(product, request.getStatus(), false);
        product.setWeight(request.getWeight());
        product.setHeight(request.getHeight());
        product.setLength(request.getLength());
        product.setWidth(request.getWidth());

        imageProductService.updateImageProduct(imagesProduct, product, shopId);
        productInformationService.updateProductInformation(productInformations);
        propertyValueService.updatePropertyValues(propertyValues);
        variantService.updateVariant(variants);

        saveProduct(product);
    }

    public void updateProductStatusByShop(String productId, Integer status, boolean isDeleted) {
        Product product = fetchById(productId);
        String shopId = shopService.fetchByUserLogin().getId();
        checkPermissionOnProduct(product, shopId);
        checkProductDeleted(product);
        updateProductStatus(product, status, isDeleted);
    }

    // ==========================================================
    // Status / Permission helpers
    // ==========================================================
    private void updateProductStatus(Product product, Integer newStatus, boolean isDeleted) {
        if (!isDeleted) {
            if (newStatus == null || (newStatus != 0 && newStatus != 1)) {
                throw new BadRequestException("Invalid status value. Allowed values: 0, 1");
            }
            product.setStatus(newStatus);
            saveProduct(product);
        } else {
            product.setStatus(2);
            saveProduct(product);
        }
    }

    public void checkPermissionOnProduct(Product product, String shopId) {
        if (!product.getShop().getId().equals(shopId)) {
            throw new AccessDeniedException(ResponseConstants.ACCESS_DENIED_MESSAGE);
        }
    }

    public void checkProductDeleted(Product product) {
        if (product.getStatus() == 2) {
            throw new BadRequestException("Product is deleted");
        }
    }

    public BigDecimal getLowestPrice(Product product) {
        if (product == null || product.getVariants() == null || product.getVariants().isEmpty()) {
            return null;
        }

        return product.getVariants().stream()
                .filter(v -> v.getPrice() != null)
                .map(Variant::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public int getProductLocked(String shopId) {
        return productRepo.countByShop_IdAndStatus(shopId, 3);
    }

    @Async
    public void pushPendingProductAsync(Product product) {

        String keyRedisProductPending = "peshop:ApproveProduct";
        int maxWaitSeconds = 30;
        int waited = 0;

        while (externalJobService.checkHandleProduct()) {
            try {
                Thread.sleep(5000);
                waited += 5;
                if (waited >= maxWaitSeconds) {
                    System.out.println("Timeout waiting for handle product.");
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        List<ProductDtoForBot> products = redisService.getList(keyRedisProductPending, ProductDtoForBot.class);

        if (products == null) {
            products = new ArrayList<>();
        }

        products.add(new ProductDtoForBot(product.getId(), product.getName(), product.getImgMain()));
        redisService.setObject(keyRedisProductPending, products, 3600);
    }

    // ==========================================================
    // Export Excel methods
    // ==========================================================
    @Transactional(readOnly = true)
    public byte[] exportProductsToExcel() throws IOException {
        String shopId = shopService.fetchByUserLogin().getId();
        List<Product> products = productRepo.findAllByShopIdForExport(shopId);

        // Initialize variants và variantValues để tránh LazyInitializationException
        // Vì Product và Variant đã có @Fetch(FetchMode.SUBSELECT), nên chỉ cần access là sẽ fetch
        for (Product product : products) {
            if (product.getVariants() != null) {
                product.getVariants().size(); // Initialize variants
                for (Variant variant : product.getVariants()) {
                    if (variant.getVariantValues() != null) {
                        variant.getVariantValues().size(); // Initialize variantValues
                        // Initialize propertyValue và propertyProduct
                        for (var variantValue : variant.getVariantValues()) {
                            if (variantValue.getPropertyValue() != null) {
                                variantValue.getPropertyValue().getValue(); // Initialize propertyValue
                            }
                            if (variantValue.getPropertyProduct() != null) {
                                variantValue.getPropertyProduct().getName(); // Initialize propertyProduct
                            }
                        }
                    }
                }
            }
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = { "Product ID", "Name", "Phân loại hàng", "Price", "Quantity", "Variant ID" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = 1;
        for (Product product : products) {
            if (product.getVariants() == null || product.getVariants().isEmpty()) {
                // Product không có variant, vẫn tạo 1 dòng với thông tin product
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(""); // Phân loại hàng trống
                row.createCell(3).setCellValue(product.getPrice() != null ? product.getPrice().doubleValue() : 0);
                row.createCell(4).setCellValue(0); // Quantity
                row.createCell(5).setCellValue(""); // Variant ID
            } else {
                // Mỗi variant = 1 dòng
                for (Variant variant : product.getVariants()) {
                    Row row = sheet.createRow(rowNum++);
                    
                    // Product ID
                    row.createCell(0).setCellValue(product.getId());
                    
                    // Name
                    row.createCell(1).setCellValue(product.getName());
                    
                    // Phân loại hàng - lấy tất cả PropertyValue values từ variantValues
                    String phanLoaiHang = "";
                    if (variant.getVariantValues() != null && !variant.getVariantValues().isEmpty()) {
                        phanLoaiHang = variant.getVariantValues().stream()
                                .sorted(Comparator.comparing(vv -> 
                                    vv.getPropertyValue() != null && vv.getPropertyValue().getLevel() != null 
                                        ? vv.getPropertyValue().getLevel() 
                                        : 0))
                                .map(vv -> vv.getPropertyValue() != null ? vv.getPropertyValue().getValue() : "")
                                .filter(value -> !value.isEmpty())
                                .collect(Collectors.joining(", "));
                    }
                    row.createCell(2).setCellValue(phanLoaiHang);
                    
                    // Price
                    row.createCell(3).setCellValue(variant.getPrice() != null ? variant.getPrice().doubleValue() : 0);
                    
                    // Quantity
                    row.createCell(4).setCellValue(variant.getQuantity() != null ? variant.getQuantity() : 0);
                    
                    // Variant ID
                    row.createCell(5).setCellValue(variant.getId() != null ? variant.getId().toString() : "");
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    @Transactional
    public void importProductsFromExcel(java.io.InputStream inputStream) throws IOException {
        String shopId = shopService.fetchByUserLogin().getId();
        
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        
        // Skip header row (row 0)
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();
        
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            
            try {
                // Read cells: Product ID (0), Name (1), Phân loại hàng (2), Price (3), Quantity (4), Variant ID (5)
                String productId = getCellValueAsString(row.getCell(0));
                String name = getCellValueAsString(row.getCell(1));
                String priceStr = getCellValueAsString(row.getCell(3));
                String quantityStr = getCellValueAsString(row.getCell(4));
                String variantIdStr = getCellValueAsString(row.getCell(5));
                
                // Validate required fields
                if (productId == null || productId.isEmpty()) {
                    errors.add("Row " + (i + 1) + ": Product ID is required");
                    failCount++;
                    continue;
                }
                
                if (variantIdStr == null || variantIdStr.isEmpty()) {
                    errors.add("Row " + (i + 1) + ": Variant ID is required");
                    failCount++;
                    continue;
                }
                
                // Parse variant ID
                Integer variantId;
                try {
                    variantId = Integer.parseInt(variantIdStr);
                } catch (NumberFormatException e) {
                    errors.add("Row " + (i + 1) + ": Invalid Variant ID format");
                    failCount++;
                    continue;
                }
                
                // Fetch and check permission for product
                Product product = fetchById(productId);
                checkPermissionOnProduct(product, shopId);
                checkProductDeleted(product);
                
                // Fetch and check permission for variant
                Variant variant = variantService.fetchById(variantId);
                variantService.checkPermissionOnVariant(variant, shopId);
                
                // Verify variant belongs to product
                if (!variant.getProduct().getId().equals(productId)) {
                    errors.add("Row " + (i + 1) + ": Variant does not belong to this product");
                    failCount++;
                    continue;
                }
                
                // Update product name if provided
                if (name != null && !name.isEmpty()) {
                    product.setName(name);
                    product.setSlug(CommonUtil.toSlug(name));
                    saveProduct(product);
                }
                
                // Update variant price if provided
                if (priceStr != null && !priceStr.isEmpty()) {
                    try {
                        BigDecimal price = new BigDecimal(priceStr);
                        if (price.compareTo(BigDecimal.ZERO) < 0) {
                            errors.add("Row " + (i + 1) + ": Price must be >= 0");
                            failCount++;
                            continue;
                        }
                        variant.setPrice(price);
                    } catch (NumberFormatException e) {
                        errors.add("Row " + (i + 1) + ": Invalid Price format");
                        failCount++;
                        continue;
                    }
                }
                
                // Update variant quantity if provided
                if (quantityStr != null && !quantityStr.isEmpty()) {
                    try {
                        Integer quantity = Integer.parseInt(quantityStr);
                        if (quantity < 0) {
                            errors.add("Row " + (i + 1) + ": Quantity must be >= 0");
                            failCount++;
                            continue;
                        }
                        variant.setQuantity(quantity);
                    } catch (NumberFormatException e) {
                        errors.add("Row " + (i + 1) + ": Invalid Quantity format");
                        failCount++;
                        continue;
                    }
                }
                
                // Save variant
                variantService.saveAll(List.of(variant));
                
                // Update product price if needed (lowest variant price)
                BigDecimal lowestPrice = getLowestPrice(product);
                if (lowestPrice != null) {
                    product.setPrice(lowestPrice);
                    saveProduct(product);
                }
                
                successCount++;
                
            } catch (Exception e) {
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
                failCount++;
            }
        }
        
        workbook.close();
        
        if (failCount > 0 && errors.size() > 0) {
            String errorMessage = String.join("; ", errors);
            throw new BadRequestException("Import completed with errors. Success: " + successCount + ", Failed: " + failCount + ". Errors: " + errorMessage);
        }
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // Check if it's an integer
            double numericValue = cell.getNumericCellValue();
            if (numericValue == Math.floor(numericValue)) {
                return String.valueOf((long) numericValue);
            } else {
                return String.valueOf(numericValue);
            }
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            // Handle formula cells
            try {
                return cell.getStringCellValue().trim();
            } catch (Exception e) {
                return String.valueOf(cell.getNumericCellValue());
            }
        }
        
        return null;
    }
}