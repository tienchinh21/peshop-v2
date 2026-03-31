package xjanua.backend.controller.shop;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.shop.ShopCreateDto;
import xjanua.backend.dto.shop.ShopDetailDtoByMe;
import xjanua.backend.dto.shop.ShopUpdateDto;
import xjanua.backend.dto.shop.WalletBalanceDto;
import xjanua.backend.mapper.ShopMapper;
import xjanua.backend.service.shop.ShopService;

@RestController
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;
    private final ShopMapper shopMapper;

    public ShopController(ShopService shopService, ShopMapper shopMapper) {
        this.shopService = shopService;
        this.shopMapper = shopMapper;
    }

    @PreAuthorize("hasAuthority('Shop')")
    @GetMapping("/me")
    public ResponseEntity<RestResponse<ShopDetailDtoByMe>> getShopByUser() {
        var dto = shopMapper.toShopDetailDtoByMe(shopService.fetchByUserLogin());
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('Shop')")
    @GetMapping("/wallet")
    public ResponseEntity<RestResponse<WalletBalanceDto>> getWalletBalance() {
        WalletBalanceDto dto = shopService.getWalletBalance();
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @PreAuthorize("hasAuthority('User')")
    @PostMapping
    public ResponseEntity<RestResponse<Void>> createShop(
            @Valid @RequestPart("data") ShopCreateDto shopCreateDto, @RequestPart("logofile") MultipartFile logofile)
            throws IOException {
        shopService.createShop(shopCreateDto, logofile);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Shop')")
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> updateShop(@PathVariable("id") String id,
            @Valid @RequestPart("data") ShopUpdateDto shopUpdateDto, @RequestPart("logofile") MultipartFile logofile)
            throws IOException {
        shopService.updateShop(id, shopUpdateDto, logofile);
        return ResponseEntity.noContent().build();
    }
}
