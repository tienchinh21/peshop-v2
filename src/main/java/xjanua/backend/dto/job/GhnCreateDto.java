package xjanua.backend.dto.job;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GhnCreateDto {
    private String district_id;
    private String ward_code;
    private String name;
    private String phone;
    private String address;
}