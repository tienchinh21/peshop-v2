package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xjanua.backend.model.ProductInformation;

@Repository
public interface ProductInformationRepo extends JpaRepository<ProductInformation, Integer> {
}