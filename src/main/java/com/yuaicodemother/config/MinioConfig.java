package com.yuaicodemother.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Minio配置类，用于配置Minio对象存储的相关信息
 * 通过@ConfigurationProperties(prefix = "minio")注解，将application.yml中的配置信息自动绑定到该类的属性中
 */
@Data
@Configuration
@Slf4j
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    // Minio服务端点
    private String endpoint;
    // 访问密钥
    private String accessKey;
    // 秘密密钥
    private String secretKey;
    // 存储桶名称
    private String bucketName;
    private Integer port;

    /**
     * 创建并配置MinioClient实例
     * 该方法利用Minio配置信息初始化一个MinioClient对象，用于与Minio服务器进行交互
     * @return MinioClient实例，用于执行桶和对象的操作
     */
    @Bean
    public MinioClient minioClient(){
        log.info("endpoint : " + endpoint);
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey,secretKey)
                .build();
    }


}
