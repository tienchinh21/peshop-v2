package xjanua.backend.service.shop;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import xjanua.backend.model.UserViewProduct;
import xjanua.backend.repository.UserViewProductRepo;

@Service
@RequiredArgsConstructor
public class UserViewProductService {
    private final UserViewProductRepo userViewProductRepository;

    public List<UserViewProduct> getAllViewsByShopAndCreatedAtBetween(String shopId, Instant startDate,
            Instant endDate) {
        return userViewProductRepository.getAllViewsByShopAndCreatedAtBetween(shopId, startDate, endDate);
    }
}