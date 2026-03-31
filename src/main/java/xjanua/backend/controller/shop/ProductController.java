package xjanua.backend.controller.shop;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.product.ProductMapEntityCreateDto;
import xjanua.backend.dto.product.ProductMapEntityUpdateDto;
import xjanua.backend.dto.product.ProductResponseDetailDto;
import xjanua.backend.model.Product;
import xjanua.backend.service.shop.ProductService;

@RestController
@PreAuthorize("hasAuthority('Shop')")
@RequestMapping("/shop/product")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<RestResponse<ProductResponseDetailDto>> getProductById(@PathVariable("id") String id) {
    // String cacheKey = "peshop:product:" + id;

    // ProductDetailResponse cachedProduct = redisService.getObject(cacheKey,
    // ProductDetailResponse.class);
    // if (cachedProduct != null) {
    // return ResponseEntity.ok(RestResponse.success(cachedProduct));
    // }

    // Product product = productService.fetchById(id);
    // ProductDetailResponse response = new ProductDetailResponse(product);

    // redisService.setObject(cacheKey, response, 3600);

    // return ResponseEntity.ok(RestResponse.success(response));

    return ResponseEntity.ok(RestResponse.success(new ProductResponseDetailDto(productService.fetchByIdByShop(id))));
  }

  @Operation(description = """
      Trả về danh sách sản phẩm có thể được lọc, phân trang và sắp xếp.

      **Tham số:**
      - **page**: Số trang (bắt đầu từ 0)
      - **size**: Số phần tử mỗi trang
      - **sort**: Dạng `property,(asc|desc)` — ví dụ: `price,desc`
      - **filter**: Biểu thức lọc theo cú pháp Spring Filter.

      **Cách sử dụng filter (Spring Filter):**
      - **Toán tử so sánh:** `:`, `>`, `>=`, `<`, `<=`
        > Ví dụ: `price > 1000`, `category.name : 'phone'`
      - **Toán tử logic:** `and`, `or`, `not`
        > Ví dụ: `price > 1000 and stock > 0`
      - **So khớp chuỗi:** `~` (like), `~~` (like không phân biệt hoa thường)
        > Ví dụ: `name ~~ 'iphone'`
        > Nếu muốn so khớp **chính xác toàn bộ chuỗi**, hãy dùng `:` thay vì `~`

      **Ví dụ sử dụng:**
      1. Lọc theo tên:
         ```
         /shop/product?page=0&size=10&filter=name ~~ 'iphone'
         ```
      2. Lọc theo danh mục:
         ```
         /shop/product?page=0&size=10&filter=category.id : 'OBJECT_ID'
         ```
      3. Lọc nâng cao, có phân trang và sắp xếp:
         ```
         /shop/product?page=0&size=10&sort=price,desc&filter=category.id : 'OBJECT_ID' and price > 1000 and name ~~ 'pro'
         ```
      ---
      **Cấu trúc dữ liệu trả về (example response):**
      ```json
      {
        "error": null,
        "content": {
          "info": {
            "page": 0,
            "size": 10,
            "pages": 0,
            "total": 0
          },
          "response": [
            {
              "id": "string",
              "name": "string",
              "imgMain": "string",
              "price": 0,
              "status": 0,
              "boughtCount": 0,
              "reviewPoint": 0.0,
              "slug": "string",
              "likeCount": 0,
              "viewCount": 0,
              "reviewCount": 0,
              "category": {
                "id": "string",
                "name": "string",
                "type": "string"
              },
              "categoryChild": {
                "id": "string",
                "name": "string",
                "description": "string"
              }
            }
          ]
        }
      }
      ```
      ---
      """)
  @GetMapping
  public ResponseEntity<PaginationDTO.Response> getAll(@Filter Specification<Product> spec,
      Pageable pageable) {
    PaginationDTO.Response users = productService.fetchAllProductByShop(spec, pageable);
    return ResponseEntity.ok(users);
  }

  @PostMapping
  public ResponseEntity<RestResponse<Void>> createProduct(
      @Valid @RequestBody ProductMapEntityCreateDto productMapEntityCreateDto) throws IOException {

    productService.createProduct(productMapEntityCreateDto);

    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<RestResponse<Void>> updateProduct(@PathVariable("id") String id,
      @Valid @RequestBody ProductMapEntityUpdateDto productMapEntityUpdateDto)
      throws IOException {

    productService.updateProduct(id, productMapEntityUpdateDto);

    return ResponseEntity.noContent().build();
  }

  @Operation(description = """
      Chỉ cập nhật được giá trị 0 và 1.
      - 0: Sản phẩm dừng hoạt động.
      - 1: Sản phẩm hoạt động.
      ---
      **Ví dụ sử dụng:**
      ```
      /shop/product/123/status?status=0
      ```
      """)
  @PatchMapping("/{id}/status")
  public ResponseEntity<RestResponse<Void>> updateProductStatus(@PathVariable("id") String id,
      @RequestParam Integer status) {
    productService.updateProductStatusByShop(id, status, false);
    return ResponseEntity.ok(RestResponse.success(null));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<RestResponse<Void>> deleteProduct(@PathVariable("id") String id) {
    productService.updateProductStatusByShop(id, 2, true);
    return ResponseEntity.ok(RestResponse.success(null));
  }

  @Operation(description = """
      Xuất danh sách sản phẩm ra file Excel.
      Format: Product ID | Name | Phân loại hàng | Price | Quantity | Variant ID
      Mỗi variant = 1 dòng trong Excel.
      """)
  @GetMapping("/export")
  public ResponseEntity<ByteArrayResource> exportProducts() throws IOException {
    byte[] excelData = productService.exportProductsToExcel();
    ByteArrayResource resource = new ByteArrayResource(excelData);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.xlsx");
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(excelData.length)
        .body(resource);
  }

  @Operation(description = """
      Nhập danh sách sản phẩm từ file Excel.
      Chỉ cập nhật các bản ghi có Product ID và Variant ID hợp lệ.
      Cập nhật: Name (product), Price và Quantity (variant).
      Format: Product ID | Name | Phân loại hàng | Price | Quantity | Variant ID
      """)
  @PostMapping(value = "/import", consumes = "multipart/form-data")
  public ResponseEntity<RestResponse<Void>> importProducts(
      @RequestPart("file") MultipartFile file) throws IOException {
    
    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(RestResponse.error("File is required", "BadRequestException"));
    }
    
    String contentType = file.getContentType();
    if (contentType == null || 
        (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
         !contentType.equals("application/vnd.ms-excel"))) {
      return ResponseEntity.badRequest()
          .body(RestResponse.error("Invalid file type. Only Excel files (.xlsx, .xls) are allowed", "BadRequestException"));
    }
    
    productService.importProductsFromExcel(file.getInputStream());
    
    return ResponseEntity.ok(RestResponse.success(null));
  }
}