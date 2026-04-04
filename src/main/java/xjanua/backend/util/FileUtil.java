package xjanua.backend.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileUtil {

    public static List<String> fetchFileNames(List<String> urls) {
        return urls.stream()
                .map(dto -> {
                    String url = dto;
                    if (url == null)
                        return null;
                    return url.substring(url.lastIndexOf("/") + 1);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static String fetchFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}