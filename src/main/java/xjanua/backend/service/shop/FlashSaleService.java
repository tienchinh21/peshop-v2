package xjanua.backend.service.shop;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.FlashSale.FlashSaleDetailDto;
import xjanua.backend.dto.FlashSale.FlashSaleResponeDto;
import xjanua.backend.dto.PaginationDTO;
import xjanua.backend.mapper.FlashSaleMapper;
import xjanua.backend.model.FlashSale;
import xjanua.backend.model.FlashSaleProduct;
import xjanua.backend.repository.FlashSaleProductRepo;
import xjanua.backend.repository.FlashSaleRepo;
import xjanua.backend.util.Enum.FlashSaleEnum;
import xjanua.backend.util.PaginationUtil;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class FlashSaleService {
    private final FlashSaleRepo flashSaleRepo;
    private final FlashSaleProductRepo flashSaleProductRepo;
    private final FlashSaleMapper flashSaleMapper;

    public FlashSale findById(String id) {
        return flashSaleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.FLASH_SALE_NOT_FOUND_MESSAGE));
    }

    public List<FlashSaleResponeDto> findAllBetweenDates(Instant startDate, Instant endDate) {
        List<FlashSale> flashSales = flashSaleRepo.findAllByCreatedAtBetween(startDate, endDate);
        return flashSales.stream()
                .map(flashSaleMapper::toFlashSaleResponeDto)
                .collect(Collectors.toList());
    }

    public List<FlashSaleResponeDto> findAll() {
        List<FlashSale> flashSales = flashSaleRepo.findAll();
        return flashSales.stream()
                .map(flashSaleMapper::toFlashSaleResponeDto)
                .collect(Collectors.toList());
    }

    public PaginationDTO.Response fetchAllFlashSales(Specification<FlashSale> specification, Pageable pageable) {
        PaginationDTO.Response response = new PaginationDTO.Response();
        Page<FlashSale> flashSales = flashSaleRepo.findAll(specification, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(flashSales, pageable);

        List<FlashSaleResponeDto> flashSaleDTOs = flashSales.getContent()
                .stream()
                .map(flashSale -> {
                    FlashSaleResponeDto dto = flashSaleMapper.toFlashSaleResponeDto(flashSale);
                    // Tính số lượng sản phẩm join vào
                    Long productCount = flashSaleProductRepo.countByFlashSaleId(flashSale.getId());
                    // Tính số lượng đã bán
                    Long soldQuantity = flashSaleProductRepo.sumUsedQuantityByFlashSaleId(flashSale.getId());
                    
                    dto.setProductCount(productCount != null ? productCount : 0L);
                    dto.setSoldQuantity(soldQuantity != null ? soldQuantity : 0L);
                    return dto;
                })
                .collect(Collectors.toList());

        response.setInfo(info);
        response.setResponse(flashSaleDTOs);
        return response;
    }

    public FlashSaleDetailDto getFlashSaleDetail(String flashSaleId) {
        FlashSale flashSale = findById(flashSaleId);
        
        List<FlashSaleProduct> flashSaleProducts = flashSaleProductRepo
                .findAllByFlashSaleIdWithProduct(flashSaleId);
        
        FlashSaleDetailDto detailDto = new FlashSaleDetailDto();
        detailDto.setId(flashSale.getId());
        
        List<FlashSaleDetailDto.ProductDetailDto> productDetails = flashSaleProducts.stream()
                .map(fsp -> {
                    FlashSaleDetailDto.ProductDetailDto productDto = new FlashSaleDetailDto.ProductDetailDto();
                    productDto.setId(fsp.getProduct().getId());
                    productDto.setName(fsp.getProduct().getName());
                    productDto.setImgMain(fsp.getProduct().getImgMain());
                    productDto.setSoldQuantity(fsp.getUsedQuantity() != null ? fsp.getUsedQuantity().longValue() : 0L);
                    return productDto;
                })
                .collect(Collectors.toList());
        
        detailDto.setProducts(productDetails);
        return detailDto;
    }

    public List<FlashSale> createBulkFlashSalesForNext7Days() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusDays(1); // Bắt đầu từ ngày mai
        LocalDate endDate = today.plusDays(7); // Kết thúc ở ngày thứ 8 (7 ngày tiếp theo)
        
        List<FlashSale> flashSales = new ArrayList<>();
        ZoneId zoneId = ZoneId.systemDefault();
        
        // Tạo flash sale cho 7 ngày (từ ngày 2 đến ngày 8)
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Tạo 8 khung giờ mỗi ngày: 0-3, 3-6, 6-9, 9-12, 12-15, 15-18, 18-21, 21-24
            for (int hour = 0; hour < 24; hour += 3) {
                LocalTime startTime = LocalTime.of(hour, 0);
                LocalTime endTime;
                
                // Xử lý khung giờ cuối cùng (21-24): dùng LocalTime.MAX thay vì 24:00
                if (hour == 21) {
                    endTime = LocalTime.MAX; // 23:59:59.999999999
                } else {
                    endTime = LocalTime.of(hour + 3, 0);
                }
                
                Instant startInstant = date.atTime(startTime).atZone(zoneId).toInstant();
                Instant endInstant = date.atTime(endTime).atZone(zoneId).toInstant();
                
                FlashSale flashSale = FlashSale.builder()
                        .startTime(startInstant)
                        .endTime(endInstant)
                        .status(FlashSaleEnum.status.INACTIVE.getValue())
                        .build();
                
                flashSales.add(flashSale);
            }
        }
        
        return flashSaleRepo.saveAll(flashSales);
    }
}