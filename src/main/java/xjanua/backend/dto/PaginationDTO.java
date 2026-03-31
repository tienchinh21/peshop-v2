package xjanua.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "PaginationDTO")
public class PaginationDTO {

    @Getter
    @Setter
    @Schema(name = "PaginationInfo")
    public static class Info {
        private int page;
        private int size;
        private int pages;
        private long total;
    }

    @Getter
    @Setter
    @Schema(name = "PaginationResponse")
    public static class Response {
        private Info info;
        private Object response;
    }
}