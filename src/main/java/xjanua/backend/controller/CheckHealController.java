package xjanua.backend.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class CheckHealController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        String timestamp = Instant.now().toString();
        System.out.println(timestamp + " pong");
        return ResponseEntity.ok("pong");
    }
}