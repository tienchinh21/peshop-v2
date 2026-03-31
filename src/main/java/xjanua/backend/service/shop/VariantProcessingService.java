package xjanua.backend.service.shop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.propertyValue.PropertyValueCreateDto;
import xjanua.backend.dto.variant.VariantCreateDtoMapWithKeys;
import xjanua.backend.dto.variantValue.VariantValueCreateDto;
import xjanua.backend.model.Product;
import xjanua.backend.model.PropertyValue;
import xjanua.backend.model.Variant;

@Service
@RequiredArgsConstructor
public class VariantProcessingService {

    private final VariantService variantService;
    private final PropertyValueService propertyValueService;
    private final VariantValueService variantValueService;

    /**
     * Validate input cho variants dựa trên variantLevel
     */
    public void validateVariantInput(
            Integer classify,
            List<PropertyValueCreateDto> propertyValues,
            List<VariantCreateDtoMapWithKeys> variants) {

        // --- Kiểm tra cơ bản ---
        if (classify == null) {
            throw new IllegalArgumentException("classify không được null");
        }
        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException("classify không được để trống");
        }

        // --- Level 0 ---
        if (classify == 0) {
            if (propertyValues != null) {
                throw new IllegalArgumentException(
                        "Khi classify = 0, propertyValues phải là null (không được truyền vào)");
            }

            if (variants.size() != 1) {
                throw new IllegalArgumentException("Khi classify = 0, chỉ được phép truyền đúng 1 variant");
            }

            for (VariantCreateDtoMapWithKeys variant : variants) {
                if (variant.getCode() != null) {
                    throw new IllegalArgumentException("Khi classify = 0, mỗi variant.code phải là null");
                }
            }
            return;
        }

        // --- Level 1 ---
        if (classify == 1) {
            if (propertyValues == null || propertyValues.isEmpty()) {
                throw new IllegalArgumentException("Khi classify = 1, propertyValues là bắt buộc");
            }

            for (PropertyValueCreateDto pv : propertyValues) {
                if (pv.getLevel() == null || pv.getLevel() != 0) {
                    throw new IllegalArgumentException("Khi classify = 1, tất cả propertyValues.level phải = 0");
                }
            }

            for (VariantCreateDtoMapWithKeys variant : variants) {
                List<Integer> codes = variant.getCode();
                if (codes == null || codes.size() != 1) {
                    throw new IllegalArgumentException("Khi classify = 1, mỗi variant.code phải có đúng 1 giá trị");
                }
            }
            return;
        }

        // --- Level 2 ---
        if (classify == 2) {
            if (propertyValues == null || propertyValues.isEmpty()) {
                throw new IllegalArgumentException("Khi classify = 2, propertyValues là bắt buộc");
            }

            boolean hasLevel0 = false;
            boolean hasLevel1 = false;
            for (PropertyValueCreateDto pv : propertyValues) {
                if (pv.getLevel() == null) {
                    throw new IllegalArgumentException("PropertyValue.level không được null");
                }
                if (pv.getLevel() == 0)
                    hasLevel0 = true;
                else if (pv.getLevel() == 1)
                    hasLevel1 = true;
                else {
                    throw new IllegalArgumentException(
                            "Khi classify = 2, propertyValues.level chỉ được là 0 hoặc 1");
                }
            }

            if (!hasLevel0 || !hasLevel1) {
                throw new IllegalArgumentException(
                        "Khi classify = 2, phải có ít nhất 1 propertyValue.level = 0 và 1 propertyValue.level = 1");
            }

            for (VariantCreateDtoMapWithKeys variant : variants) {
                List<Integer> codes = variant.getCode();
                if (codes == null || codes.size() != 2) {
                    throw new IllegalArgumentException("Khi classify = 2, mỗi variant.code phải có đúng 2 giá trị");
                }
            }
            return;
        }

        // --- Nếu classify không nằm trong [0,2] ---
        throw new IllegalArgumentException("classify không hợp lệ. Chỉ được phép là 0, 1, hoặc 2.");
    }

    /**
     * Xử lý tạo variants dựa trên variantLevel
     */
    @Transactional
    public void processVariants(
            List<PropertyValueCreateDto> propertyValues,
            List<VariantCreateDtoMapWithKeys> variants,
            Product product,
            Integer classify) {

        // Level 0: Không có PropertyValue, chỉ tạo variant
        if (classify == 0) {
            createVariantsOnly(variants.get(0), product);
            return;
        }

        // Level 1, 2: Tạo PropertyValue và map với variants
        if (propertyValues == null || propertyValues.isEmpty()) {
            throw new IllegalArgumentException("PropertyValues không được null khi classify > 0");
        }

        List<PropertyValue> savedValues = propertyValueService.createListPropertyValue(propertyValues);
        List<VariantValueCreateDto> variantValueDtos = createVariantsAndValues(variants, product, savedValues,
                classify);
        variantValueService.createVariantValues(variantValueDtos);
    }

    /**
     * Tạo variant mà không có PropertyValue (variantLevel = 0)
     */
    private void createVariantsOnly(VariantCreateDtoMapWithKeys variant, Product product) {
        if (product.getVariants() == null) {
            product.setVariants(new ArrayList<>());
        }
        Variant variantCreated = variantService.createVariant(variant.getVariantCreateDto(), product);
        product.getVariants().add(variantCreated);
    }

    /**
     * Tạo variants và map với PropertyValues (variantLevel = 1 hoặc 2)
     */
    private List<VariantValueCreateDto> createVariantsAndValues(
            List<VariantCreateDtoMapWithKeys> variants,
            Product product,
            List<PropertyValue> savedValues,
            Integer classify) {

        List<VariantValueCreateDto> variantValueDtos = new ArrayList<>();

        if (product.getVariants() == null) {
            product.setVariants(new ArrayList<>());
        }

        for (VariantCreateDtoMapWithKeys variantDto : variants) {
            Variant variant = variantService.createVariant(variantDto.getVariantCreateDto(), product);
            product.getVariants().add(variant);

            List<Integer> codes = variantDto.getCode();
            if (codes == null || codes.isEmpty()) {
                throw new IllegalArgumentException(
                        "Variant code không được null khi variantLevel > 0");
            }

            // Validate số lượng codes phải khớp với variantLevel
            if ((classify == 1 && codes.size() != 1) || (classify == 2 && codes.size() != 2)) {
                throw new IllegalArgumentException(
                        String.format("Số lượng codes (%d) không khớp với classify (%d)",
                                codes.size(), classify));
            }

            List<PropertyValue> matched = matchPropertyValuesByCodes(codes, savedValues, classify);

            if (matched.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("Không tìm thấy PropertyValue tương ứng với codes: %s", codes));
            }

            VariantValueCreateDto dto = VariantValueCreateDto.builder()
                    .variant(variant)
                    .propertyValues(matched)
                    .build();
            variantValueDtos.add(dto);
        }

        return variantValueDtos;
    }

    /**
     * Match PropertyValues theo codes và variantLevel
     * 
     * @param codes        Danh sách codes (theo thứ tự level 0, level 1...)
     * @param savedValues  Danh sách PropertyValue đã lưu
     * @param variantLevel Level của variant (1 hoặc 2)
     * @return Danh sách PropertyValue matched
     */
    private List<PropertyValue> matchPropertyValuesByCodes(
            List<Integer> codes,
            List<PropertyValue> savedValues,
            Integer classify) {

        if (codes == null || codes.isEmpty()) {
            throw new IllegalArgumentException("Codes không được null hoặc empty");
        }

        if (classify == null || (classify != 1 && classify != 2)) {
            throw new IllegalArgumentException(
                    "variantLevel phải là 1 hoặc 2 khi gọi matchPropertyValuesByCodes");
        }

        List<PropertyValue> matched = new ArrayList<>();

        // Level 1: codes[0] -> level 0
        if (classify == 1) {
            Integer code = codes.get(0);
            for (PropertyValue pv : savedValues) {
                if (pv.getLevel() != null && pv.getLevel() == 0 && pv.getCode().equals(code)) {
                    matched.add(pv);
                    break;
                }
            }
            return matched;
        }

        // Level 2: codes[0] -> level 0, codes[1] -> level 1
        if (classify == 2) {
            // Match level 0
            Integer codeLevel0 = codes.get(0);
            for (PropertyValue pv : savedValues) {
                if (pv.getLevel() != null && pv.getLevel() == 0 && pv.getCode().equals(codeLevel0)) {
                    matched.add(pv);
                    break;
                }
            }

            // Match level 1
            Integer codeLevel1 = codes.get(1);
            for (PropertyValue pv : savedValues) {
                if (pv.getLevel() != null && pv.getLevel() == 1 && pv.getCode().equals(codeLevel1)) {
                    matched.add(pv);
                    break;
                }
            }

            // Validate đã match đủ cả 2 levels
            if (matched.size() != 2) {
                throw new IllegalArgumentException(
                        String.format("Không tìm thấy đủ PropertyValue cho codes: %s. Chỉ match được %d/2",
                                codes, matched.size()));
            }

            return matched;
        }

        return matched;
    }
}
