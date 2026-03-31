package xjanua.backend.dto.variant;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantCreateDtoMapWithKeys {

    @Valid
    @NotNull(message = "Variant is required")
    private VariantCreateDto variantCreateDto;

    private List<Integer> code;
}