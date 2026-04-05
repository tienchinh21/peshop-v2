package xjanua.backend.service.shop;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import xjanua.backend.model.UserViewShop;
import xjanua.backend.repository.UserViewShopRepo;

@Service
@RequiredArgsConstructor
public class UserViewShopService {
    private final UserViewShopRepo userViewShopRepo;

    public List<UserViewShop> getUserViewShops(String shopId, Instant start, Instant end) {
        return userViewShopRepo.findAllByShop_IdAndCreatedAtBetween(shopId, start, end);
    }

    public List<UserViewShop> getUserViewShopsDistinctByUserId(String shopId, Instant start, Instant end) {
        List<UserViewShop> allViews = getUserViewShops(shopId, start, end);

        Map<String, UserViewShop> distinctByUserId = new LinkedHashMap<>();

        for (UserViewShop view : allViews) {
            String userId = view.getUser().getId();
            distinctByUserId.putIfAbsent(userId, view);
        }

        return distinctByUserId.values().stream()
                .collect(Collectors.toList());
    }
}