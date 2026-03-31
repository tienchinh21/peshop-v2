package xjanua.backend.service.shop;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import xjanua.backend.dto.variantValue.VariantValueCreateDto;
import xjanua.backend.model.VariantValue;
import xjanua.backend.repository.VariantValueRepo;
import xjanua.backend.util.exception.BadRequestException;

@Service
public class VariantValueService {
        private final VariantValueRepo variantValueRepo;

        public VariantValueService(VariantValueRepo variantValueRepo) {
                this.variantValueRepo = variantValueRepo;
        }

        /**
         * Tạo nhiều VariantValue cùng lúc để tối ưu hiệu suất
         */
        public List<VariantValue> createVariantValues(List<VariantValueCreateDto> requests) {
                if (requests == null || requests.isEmpty()) {
                        throw new BadRequestException("Variant value requests are required");
                }
                List<VariantValue> variantValues = requests.stream()
                                .flatMap(request -> {
                                        // Kiểm tra propertyValueIds có tồn tại và không empty
                                        if (request.getPropertyValues() == null
                                                        || request.getPropertyValues().isEmpty()) {
                                                return java.util.stream.Stream.empty();
                                        }

                                        // Tạo nhiều VariantValue cho mỗi propertyValueId trong list
                                        return request.getPropertyValues().stream()
                                                        .map(propertyValue -> {

                                                                return VariantValue.builder()
                                                                                .variant(request.getVariant())
                                                                                .propertyProduct(propertyValue
                                                                                                .getPropertyProduct())
                                                                                .propertyValue(propertyValue)
                                                                                .build();
                                                        });
                                })
                                .collect(Collectors.toList());

                return variantValueRepo.saveAll(variantValues);
        }
}