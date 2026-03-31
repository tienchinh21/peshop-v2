package xjanua.backend.controller.shop;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.FlashSale.FlashSaleDetailDto;
import xjanua.backend.dto.FlashSale.FlashSaleGroupDto;
import xjanua.backend.dto.FlashSale.FlashSaleResponeDto;
import xjanua.backend.dto.FlashSale.FlashSaleProduct.FlashSaleProductCreateDto;
import xjanua.backend.model.FlashSale;
import xjanua.backend.service.shop.FlashSaleProductService;
import xjanua.backend.service.shop.FlashSaleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/flash-sale")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;
    private final FlashSaleProductService flashSaleProductService;

    @Value("${api.key}")
    private String apiKey;

    @PreAuthorize("hasAuthority('Shop')")
    @GetMapping
    public ResponseEntity<RestResponse<List<FlashSaleResponeDto>>> getAllFlashSales(@RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        Instant start = startDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant end = endDate
                .atTime(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        List<FlashSaleResponeDto> flashSales = flashSaleService.findAllBetweenDates(start, end);
        return ResponseEntity.ok(RestResponse.success(flashSales));
    }

    @PreAuthorize("hasAuthority('Shop')")
    @PostMapping("/join/{flashSaleId}")
    public ResponseEntity<RestResponse<Void>> joinFlashSale(
            @PathVariable("flashSaleId") String flashSaleId,
            @Valid @RequestBody List<FlashSaleProductCreateDto> requests) {
        flashSaleProductService.joinFlashSales(flashSaleId, requests);
        return ResponseEntity.ok(RestResponse.success(null));
    }

    @PreAuthorize("hasAuthority('Shop')")
    @GetMapping("/participated")
    public ResponseEntity<RestResponse<List<FlashSaleGroupDto>>> getParticipatedFlashSales() {
        List<FlashSaleGroupDto> flashSaleGroups = flashSaleProductService.getFlashSaleProducts();
        return ResponseEntity.ok(RestResponse.success(flashSaleGroups));
    }

    @PostMapping("/bulk-create")
    public ResponseEntity<RestResponse<Integer>> bulkCreateFlashSales(
            @RequestHeader("API-KEY") String providedApiKey) {
        
        if (!providedApiKey.equals(this.apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RestResponse.error("Invalid API key", "Invalid API key"));
        }

        List<FlashSale> createdFlashSales = flashSaleService.createBulkFlashSalesForNext7Days();
        return ResponseEntity.ok(RestResponse.success(createdFlashSales.size()));
    }

    @GetMapping("/all")
    public ResponseEntity<RestResponse<PaginationDTO.Response>> getAllFlashSales(
            @RequestHeader("API-KEY") String providedApiKey,
            @Filter Specification<FlashSale> spec,
            Pageable pageable) {
        
        if (!providedApiKey.equals(this.apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RestResponse.error("Invalid API key", "Invalid API key"));
        }

        PaginationDTO.Response response = flashSaleService.fetchAllFlashSales(spec, pageable);
        return ResponseEntity.ok(RestResponse.success(response));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<RestResponse<FlashSaleDetailDto>> getFlashSaleDetail(
            @RequestHeader("API-KEY") String providedApiKey,
            @PathVariable("id") String id) {
        
        if (!providedApiKey.equals(this.apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RestResponse.error("Invalid API key", "Invalid API key"));
        }

        FlashSaleDetailDto detail = flashSaleService.getFlashSaleDetail(id);
        return ResponseEntity.ok(RestResponse.success(detail));
    }
}