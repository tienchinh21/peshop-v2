package xjanua.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import xjanua.backend.service.interfaces.StorageService;

@RestController
@RequestMapping("/storage")
public class StorageController {

    private final StorageService storageService;

    @Value("${api.key}")
    private String apiKey;

    public StorageController(@Qualifier("localStorageServiceImpl") StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/upload/temp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = storageService.saveFile(file, "temp");
        return ResponseEntity.ok(url);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file,
            @RequestParam("subPath") String subPath, @RequestHeader("API-KEY") String apiKey) throws IOException {
        if (!apiKey.equals(this.apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API key");
        }
        String url = storageService.saveFile(file, subPath);
        return ResponseEntity.ok(url);
    }

    // @DeleteMapping("/delete")
    // public ResponseEntity<Void> deleteFile(@RequestParam("url") String url) {
    // storageService.deleteFile(url);
    // return ResponseEntity.ok().build();
    // }
}