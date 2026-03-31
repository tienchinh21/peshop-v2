package xjanua.backend.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.service.LogStatisticsService;

@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {

    private final LogStatisticsService logStatisticsService;

    @Value("${api.key}")
    private String apiKey;

    @GetMapping("/log-statistics")
    public ResponseEntity<RestResponse<Long>> getLogStatistics(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            @RequestHeader("API-KEY") String apiKey) throws Exception {

        if (!apiKey.equals(this.apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RestResponse.error("Invalid API key", "Invalid API key"));
        }

        long total = logStatisticsService.countRequestsInDateRange(startDate, endDate);
        return ResponseEntity.ok(RestResponse.success(total));
    }
}