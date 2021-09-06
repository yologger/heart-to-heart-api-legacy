package com.yologger.heart_to_heart_springboot.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Log4j2
public class AwsS3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String upload(MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename();

        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + originalFileName;

        String dateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = dateFormat.replace("/", File.separator);

        String filePath = folderPath + "/" + fileName;

        amazonS3.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3.getUrl(bucket, filePath).toString();
    }

    public void delete(String filePath) throws SdkClientException, AmazonServiceException {
        boolean isExistingObject = amazonS3.doesObjectExist(bucket, filePath);
        if (isExistingObject) {
            amazonS3.deleteObject(bucket, filePath);
        }
    }
}
