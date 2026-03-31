package xjanua.backend.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LogStatisticsService {

    private final String logFilePath;

    public LogStatisticsService(@Value("${app.log-file-traffic-path}") String logFilePath) {
        this.logFilePath = logFilePath;
    }

    /**
     * Đếm số lượng request trong khoảng thời gian từ startDate đến endDate
     * 
     * @param startDate Ngày bắt đầu (bao gồm)
     * @param endDate   Ngày kết thúc (bao gồm)
     * @return Số lượng request (long)
     */
    public long countRequestsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        long count = 0;

        // DateTimeFormatter để parse format: 09/Dec/2025:19:32:58
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Parse ngày tháng từ dòng log
                // Format: 09/Dec/2025:19:32:58 +0700 172.68.164.163 "GET /spring-peshop/shop/me
                // HTTP/1.1"
                LocalDateTime logDateTime = parseLogDateTime(line, formatter);

                if (logDateTime != null) {
                    // Kiểm tra nếu logDateTime nằm trong khoảng startDate và endDate (bao gồm cả 2
                    // đầu)
                    if (!logDateTime.isBefore(startDate) && !logDateTime.isAfter(endDate)) {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading log file: " + logFilePath, e);
        }

        return count;
    }

    /**
     * Parse ngày tháng từ dòng log
     * Format: 09/Dec/2025:19:32:58 +0700 ...
     */
    private LocalDateTime parseLogDateTime(String logLine, DateTimeFormatter formatter) {
        try {
            // Tìm vị trí của khoảng trắng đầu tiên sau datetime (trước timezone)
            // Format: dd/MMM/yyyy:HH:mm:ss +0700 ...
            // Tìm pattern: khoảng trắng + dấu + hoặc - (timezone)
            int spaceIndex = -1;
            for (int i = 0; i < logLine.length() - 1; i++) {
                if (logLine.charAt(i) == ' ' &&
                        (logLine.charAt(i + 1) == '+' || logLine.charAt(i + 1) == '-')) {
                    spaceIndex = i;
                    break;
                }
            }

            if (spaceIndex == -1) {
                return null;
            }

            // Extract phần datetime: 09/Dec/2025:19:32:58
            String dateTimeStr = logLine.substring(0, spaceIndex);

            // Parse thành LocalDateTime
            return LocalDateTime.parse(dateTimeStr, formatter);

        } catch (DateTimeParseException | StringIndexOutOfBoundsException e) {
            // Nếu không parse được, bỏ qua dòng này
            return null;
        }
    }
}
