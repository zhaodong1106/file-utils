package com.zhaodong.fileutils.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnProperty(prefix = "minio",name = "controller",havingValue = "true")
@ConditionalOnClass({io.minio.MinioClient.class})
public class MyMinIoConfig {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.port}")
    private int  port;
    @Value("${minio.accessKey}")
    private String  accessKey;
    @Value("${minio.secretKey}")
    private String  secretKey;

    @Bean
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(endpoint,port,accessKey,secretKey);
    }
}
