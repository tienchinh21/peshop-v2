package xjanua.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xjanua.backend.model.PropertyValue;

public interface PropertyValueRepo extends JpaRepository<PropertyValue, String> {
}
