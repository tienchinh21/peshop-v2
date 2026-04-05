package xjanua.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import xjanua.backend.model.OrderVoucher;

@Repository
public interface OrderVoucherRepo extends JpaRepository<OrderVoucher, Integer>, JpaSpecificationExecutor<OrderVoucher> {
    @EntityGraph(attributePaths = { "order" })
    Page<OrderVoucher> findByVoucherShopId(String voucherShopId, Pageable pageable);
}