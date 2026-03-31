package xjanua.backend.service.shop;

import java.io.IOException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.job.GhnCreateDto;
import xjanua.backend.dto.shop.ShopCreateDto;
import xjanua.backend.dto.shop.ShopUpdateDto;
import xjanua.backend.dto.shop.WalletBalanceDto;
import xjanua.backend.model.Role;
import xjanua.backend.model.Shop;
import xjanua.backend.model.User;
import xjanua.backend.model.Wallet;
import xjanua.backend.repository.ShopRepo;
import xjanua.backend.service.RedisService;
import xjanua.backend.service.RoleService;
import xjanua.backend.service.UserService;
import xjanua.backend.service.interfaces.ExternalJobService;
import xjanua.backend.service.interfaces.StorageService;
import xjanua.backend.util.CommonUtil;
import xjanua.backend.util.SecurityUtil;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.BadRequestException;
import xjanua.backend.util.exception.ResourceAlreadyExistsException;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;
    private final UserService userService;
    private final StorageService storageService;
    private final RoleService roleService;
    private final RedisService redisService;
    private final ExternalJobService externalJobService;

    public Shop fetchById(String shopId) {
        return shopRepo.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.SHOP_NOT_FOUND_MESSAGE));
    }

    public Shop fetchByUserLogin() {
        String userId = SecurityUtil.getCurrentUserLogin();
        String cacheKey = "peshop:shop:byuser:" + userId;

        try {
            Shop cachedShop = redisService.getObject(cacheKey, Shop.class);
            if (cachedShop != null) {
                checkShopDeleted(cachedShop);
                return cachedShop;
            }
        } catch (Exception e) {
        }

        Shop shop = shopRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.SHOP_NOT_FOUND_MESSAGE));
        checkShopDeleted(shop);

        try {
            redisService.setObject(cacheKey, shop, 1800);
        } catch (Exception e) {
        }

        return shop;
    }

    @Transactional
    public Shop createShop(ShopCreateDto createShopDto, MultipartFile logoFile) throws IOException {
        User user = userService.fetchMe();

        if (user.getHasShop()) {
            throw new ResourceAlreadyExistsException(ResponseConstants.USER_SHOP_ALREADY_EXISTS_MESSAGE);
        }

        Shop shop = Shop.builder()
                .name(createShopDto.getName())
                .description(createShopDto.getDescription())
                .oldProviceId(createShopDto.getOldProviceId().orElse(null))
                .oldDistrictId(createShopDto.getOldDistrictId().orElse(null))
                .oldWardId(createShopDto.getOldWardId().orElse(null))
                .newProviceId(createShopDto.getNewProviceId().orElse(null))
                .newWardId(createShopDto.getNewWardId().orElse(null))
                .streetLine(createShopDto.getStreetLine().orElse(null))
                .fullOldAddress(createShopDto.getFullOldAddress().orElse(null))
                .fullNewAddress(createShopDto.getFullNewAddress().orElse(null))
                .status(0)
                .logoUrl(storageService.saveFile(logoFile, "images/shopLogo"))
                .user(user)
                .build();

        GhnCreateDto ghCreateDto = GhnCreateDto.builder()
                .district_id(createShopDto.getOldDistrictId().orElse(null))
                .ward_code(createShopDto.getOldWardId().orElse(null))
                .name(createShopDto.getName())
                .phone(user.getPhone())
                .address(createShopDto.getFullNewAddress().orElse(null))
                .build();

        int ghnId = callGhnCreateAndGetShopId(ghCreateDto);

        shop.setGhnId(ghnId);

        user.setHasShop(true);

        // Gán role "Shop" cho user
        Role shopRole = roleService.findByName("Shop");
        user.getRoles().add(shopRole);

        userService.save(user);
        shop = shopRepo.save(shop);
        return shop;
    }

    @Transactional
    public Shop updateShop(String shopId, ShopUpdateDto updateShopDto, MultipartFile logoFile) throws IOException {
        String userId = SecurityUtil.getCurrentUserLogin();
        Shop shop = fetchByUserLogin();
        validShopStatus(shop, updateShopDto.getStatus(), false);

        shop.setName(updateShopDto.getName());
        shop.setDescription(updateShopDto.getDescription());
        shop.setOldProviceId(updateShopDto.getOldProviceId().orElse(null));
        shop.setOldDistrictId(updateShopDto.getOldDistrictId().orElse(null));
        shop.setOldWardId(updateShopDto.getOldWardId().orElse(null));
        shop.setNewProviceId(updateShopDto.getNewProviceId().orElse(null));
        shop.setNewWardId(updateShopDto.getNewWardId().orElse(null));
        shop.setStreetLine(updateShopDto.getStreetLine().orElse(null));
        shop.setFullOldAddress(updateShopDto.getFullOldAddress().orElse(null));
        shop.setFullNewAddress(updateShopDto.getFullNewAddress().orElse(null));
        shop.setStatus(updateShopDto.getStatus());
        if (logoFile != null) {
            shop.setLogoUrl(storageService.saveFile(logoFile, "images/shopLogo"));
        }
        shop = shopRepo.save(shop);

        invalidateShopCache(userId);

        return shop;
    }

    public WalletBalanceDto getWalletBalance() {
        Shop shop = fetchByUserLogin();
        Wallet wallet = shop.getWallet();

        checkShopDeleted(shop);

        if (wallet == null) {
            throw new ResourceNotFoundException("Wallet not found for this shop");
        }

        WalletBalanceDto dto = new WalletBalanceDto();
        dto.setBalance(wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO);
        return dto;
    }

    public void validShopStatus(Shop shop, Integer newStatus, boolean isSystem) {
        checkShopDeleted(shop);

        int maxAllowed = isSystem ? 3 : 2;
        if (newStatus < 0 || newStatus > maxAllowed) {
            throw new BadRequestException(
                    "Invalid status value. Allowed values: " + (isSystem ? "0, 1, 2, 3" : "0, 1, 2"));
        }

        if (!isSystem && newStatus == 3) {
            throw new BadRequestException("You cannot update shop status to locked");
        }
    }

    private void checkShopDeleted(Shop shop) {
        if (shop.getStatus() == 2) {
            throw new BadRequestException("Shop is deleted");
        }
    }

    private void invalidateShopCache(String userId) {
        try {
            redisService.delete("peshop:shop:byuser:" + userId);
        } catch (Exception e) {
        }
    }

    private int callGhnCreateAndGetShopId(GhnCreateDto ghCreateDto) {
        String response = externalJobService.callToDotnet(
                "/ghn/create-store",
                CommonUtil.toJson(ghCreateDto));

        System.out.println("response: " + response);

        JsonNode responseJson = CommonUtil.parseTextToJson(response);

        int ghnId = responseJson.path("data").path("data").path("shop_id").asInt();

        return ghnId;
    }
}