package com.yuaicodemother.utils;

import com.yuaicodemother.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MinioUtils {
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioConfig minioConfig;

    public String uploadMultipartFile(MultipartFile file) throws  Exception {
        LocalDateTime today = LocalDateTime.now();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = today.getMonth().getValue() + "/" +  today.getDayOfMonth()
                + "/" +  UUID.randomUUID().toString().replace("-", "").substring(0, 8)
                + "_"
                + FilenameUtils.getBaseName(file.getOriginalFilename())
                + (extension.isEmpty() ? "" : "." + extension);;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(filename)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + filename;
    }

    public String uploadFile(File file) throws Exception {

        LocalDateTime today = LocalDateTime.now();
        String extension = FilenameUtils.getExtension(file.getName());
        String filename = generateFilename(file.getName(), today, extension);

        String contentType = detectContentType(file);

        try (InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(filename)
                            .stream(inputStream, file.length(), -1)
                            .contentType(contentType)
                            .build());
        }

        return buildFileUrl(filename);
    }
    private String generateFilename(String originalFilename, LocalDateTime today, String extension) {
        return today.getMonth().getValue() + "/" + today.getDayOfMonth()
                + "/" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)
                + "_"
                + FilenameUtils.getBaseName(originalFilename)
                + (extension.isEmpty() ? "" : "." + extension);
    }
    private String detectContentType(File file) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        return contentType != null ? contentType : "application/octet-stream";
    }

    private String buildFileUrl(String filename) {
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + filename;
    }
}