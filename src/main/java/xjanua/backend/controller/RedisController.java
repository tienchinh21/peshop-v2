package xjanua.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xjanua.backend.dto.RestResponse;
import xjanua.backend.service.RedisService;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisService redisService;

    @Value("${api.key}")
    private String apiKey;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/get")
    public ResponseEntity<RestResponse<String>> getRedis(
            @RequestParam("key") String key,
            @RequestHeader("API-KEY") String apiKey) {
        if (!apiKey.equals(this.apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RestResponse.error("Invalid API key", "Invalid API key"));
        }
        return ResponseEntity.ok(RestResponse.success(redisService.get(key)));
    }

    @PostMapping("/set")
    public ResponseEntity<?> setRedis(
            @RequestParam("key") String key,
            @RequestParam("value") String value,
            @RequestParam("seconds") int seconds,
            @RequestHeader("API-KEY") String apiKey) {
        if (!apiKey.equals(this.apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid API key");
        }
        return ResponseEntity.ok(redisService.set(key, value, seconds));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRedis(
            @RequestParam("key") String key,
            @RequestHeader("API-KEY") String apiKey) {
        if (!apiKey.equals(this.apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid API key");
        }
        return ResponseEntity.ok(redisService.delete(key));
    }
}
