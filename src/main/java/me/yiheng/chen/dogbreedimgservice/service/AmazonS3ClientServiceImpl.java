package me.yiheng.chen.dogbreedimgservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import me.yiheng.chen.dogbreedimgservice.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import static java.lang.String.format;

/**
 * @author Yiheng Chen
 * @date 21/1/19 11:28 PM
 */
@Component
@Slf4j
public class AmazonS3ClientServiceImpl implements AmazonS3ClientService {
    private String awsS3Bucket;
    private AmazonS3 amazonS3;

    @Autowired
    public AmazonS3ClientServiceImpl(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider,String awsS3Bucket) {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion.getName()).build();
        this.awsS3Bucket = awsS3Bucket;
    }

    @Async
    public String uploadFileToS3Bucket(File file, boolean enablePublicReadAccess) throws CustomException {
        String fileName = file.getName();
        String resourceUrl = null;

        try {

            PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3Bucket, fileName, file);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            PutObjectResult result = amazonS3.putObject(putObjectRequest);

            resourceUrl = Optional.ofNullable(amazonS3.getUrl(awsS3Bucket, fileName).getPath()).orElseThrow(() -> new CustomException("resource url cannot be null"));

            //removing the file created in the server
            if (file.delete()) {
                log.error(format("failed to delete file: {%s}", fileName));
            }
        } catch (AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
        }

        return resourceUrl;

    }
}
