package me.yiheng.chen.dogbreedimgservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.yiheng.chen.dogbreedimgservice.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

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
    public AmazonS3ClientServiceImpl(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider, String awsS3Bucket) {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion.getName()).build();
        this.awsS3Bucket = awsS3Bucket;
    }

    public String uploadFileToS3Bucket(@NonNull File file, boolean enablePublicReadAccess) throws CustomException {
        String fileName = file.getName();
        String resourceUrl = null;

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3Bucket, fileName, file);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            amazonS3.putObject(putObjectRequest);

            if (!file.delete()) {
                log.error("failed to delete file: {}", file.getName());
            }

            resourceUrl = Optional.ofNullable(String.valueOf(amazonS3.getUrl(awsS3Bucket, fileName))).orElseThrow(() -> new CustomException("resource url cannot be null"));

        } catch (AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
        }

        return resourceUrl;

    }

    @Override
    public Boolean deleteFileFromS3Bucket(String bucketName, String keyName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while removing [" + keyName + "] ");
            return false;
        }
        return true;
    }
}
