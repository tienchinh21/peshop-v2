package xjanua.backend.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import xjanua.backend.dto.PaginationDTO;

public class PaginationUtil {
    public static PaginationDTO.Info buildInfo(Page<?> page, Pageable pageable) {
        PaginationDTO.Info info = new PaginationDTO.Info();
        info.setPage(pageable.getPageNumber() + 1);
        info.setSize(pageable.getPageSize());
        info.setPages(page.getTotalPages());
        info.setTotal(page.getTotalElements());
        return info;
    }
}