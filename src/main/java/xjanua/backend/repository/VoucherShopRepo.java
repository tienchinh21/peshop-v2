package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import xjanua.backend.model.VoucherShop;

public interface VoucherShopRepo extends JpaRepository<VoucherShop, String>, JpaSpecificationExecutor<VoucherShop> {
    boolean existsByCodeAndShopIdAndStatusNot(String code, String shopId, int status);

    boolean existsByCodeAndShopIdAndIdNot(String code, String shopId, String id);
}