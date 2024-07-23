package com.file.server.fileserver.project.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig{

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials awsCreds = AwsBasicCredentials
        .create("AKIATCKAS7QIHHVU6Q4L","fpErwmqpVt1Te/mV2+ShwfKryj76uyjhpFczm46n");

        return S3Client.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
