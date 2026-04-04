package xjanua.backend.util;

import java.text.Normalizer;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toSlug(String input) {
        // Chuyển riêng đ/Đ thành d/D
        input = input.replaceAll("đ", "d").replaceAll("Đ", "d");

        String noAccent = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        String slug = noAccent.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        int randomNumber = ThreadLocalRandom.current().nextInt(0, 10000000);
        String random7Digits = String.format("%07d", randomNumber);

        return slug + "-" + random7Digits;
    }

    public static Boolean isEndTimeAfterStartTime(Instant startTime, Instant endTime) {
        if (endTime.isAfter(startTime)) {
            return true;
        }
        return false;
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    public static JsonNode parseTextToJson(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}