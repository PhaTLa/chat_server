package com.example.test_server.util;

import com.example.test_server.constants.CommonConstant;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;

public class FileUploadUtil {

    public static String saveFile(String fileName, String dirPath, MultipartFile file) throws IOException {
        Path directory = Paths.get(dirPath);

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = directory.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            if (dirPath.equals(CommonConstant.UPLOAD_DIR_PHOTO)) {
                //root path: D:\LamPT16_outsource10\test_server
                return StringUtils.cleanPath(
                        filePath.toFile().getAbsolutePath().replace(System.getProperty("user.dir"),
                                "http://10.2.216.108:8081"));
            } else {
                return String.format("%s-%d", fileName, file.getSize());
            }

        } catch (Exception ex) {
            throw new IOException("Could not save file - ", ex);
        }
    }

    public static Optional<String> getFileExtension(String fileOriginalName) {
        return Optional.ofNullable(fileOriginalName)
                .filter(name -> name.contains("."))
                .map(name -> name.substring(fileOriginalName.lastIndexOf(".") + 1));
    }

    public static MediaType getMediaTypeOfFile(ServletContext context, String fileName) {
        String mimeType = context.getMimeType(fileName);
        try {
            return MediaType.parseMediaType(mimeType);
        } catch (InvalidMediaTypeException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
