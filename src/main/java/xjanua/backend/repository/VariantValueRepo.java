package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xjanua.backend.model.VariantValue;

public interface VariantValueRepo extends JpaRepository<VariantValue, Integer> {

}
