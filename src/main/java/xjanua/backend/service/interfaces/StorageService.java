package xjanua.backend.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String saveFile(MultipartFile file, String subPath);

    List<String> saveFiles(MultipartFile[] files, String subPath);

    void delete(List<String> fileNames, String subPath);

    void delete(String fileName, String subPath);

    Boolean checkFileExists(List<String> fileNames, String subPath);

    Boolean checkFileExist(String fileName, String subPath);

    String moveFile(String fileName, String fromSubPath, String toSubPath);

    List<String> moveFiles(List<String> fileNames, String fromSubPath, String toSubPath);
}