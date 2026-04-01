package xjanua.backend.dto.variantValue;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import xjanua.backend.model.PropertyValue;
import xjanua.backend.model.Variant;

@Getter
@Setter
@Builder
public class VariantValueCreateDto {
    @NotNull(message = "Variant ID is required")
    private Variant variant;

    private List<PropertyValue> propertyValues;
}