package xjanua.backend.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import xjanua.backend.service.interfaces.StorageService;

@Service
public class LocalStorageServiceImpl implements StorageService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file, String subPath) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        try {
            String contentType = file.getContentType();
            if (!List.of("image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml")
                    .contains(contentType)) {
                throw new IllegalArgumentException("Only JPG, PNG, GIF, WebP and SVG files are allowed");
            }

            String extension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf('.') + 1);
            String fileName = System.currentTimeMillis() + "." + extension;

            Path directory = Paths.get(uploadDir, subPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path filePath = directory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + subPath + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    @Override
    public List<String> saveFiles(MultipartFile[] files, String subPath) {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
                .map(file -> saveFile(file, subPath))
                .toList();
    }

    @Override
    public void delete(String fileName, String subPath) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            Path path = Paths.get(uploadDir, subPath, fileName);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }

    @Override
    public void delete(List<String> fileNames, String subPath) {
        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }

        fileNames.forEach(fileName -> delete(fileName, subPath));
    }

    @Override
    public Boolean checkFileExist(String fileName, String subPath) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        Path path = Paths.get(uploadDir, subPath, fileName);
        return Files.exists(path);
    }

    @Override
    public Boolean checkFileExists(List<String> fileNames, String subPath) {
        if (fileNames == null || fileNames.isEmpty()) {
            return true;
        }

        for (String fileName : fileNames) {
            if (!checkFileExist(fileName, subPath)) {
                throw new RuntimeException("File does not exist: " + fileName + " in path: " + subPath);
            }
        }

        return true;
    }

    @Override
    public String moveFile(String fileName, String fromSubPath, String toSubPath) {
        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        try {
            Path sourcePath = Paths.get(uploadDir, fromSubPath, fileName);
            Path targetDir = Paths.get(uploadDir, toSubPath);

            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            Path targetPath = targetDir.resolve(fileName);

            // Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + toSubPath + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to move file: " + fileName, e);
        }
    }

    @Override
    public List<String> moveFiles(List<String> fileNames, String fromSubPath, String toSubPath) {
        if (fileNames == null || fileNames.isEmpty()) {
            return new ArrayList<>();
        }

        return fileNames.stream()
                .map(fileName -> moveFile(fileName, fromSubPath, toSubPath))
                .toList();
    }
}
