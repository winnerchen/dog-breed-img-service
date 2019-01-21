package me.yiheng.chen.dogbreedimgservice.service;

import me.yiheng.chen.dogbreedimgservice.exception.CustomException;

import java.io.File;

/**
 * @author Yiheng Chen
 * @date 21/1/19 11:27 PM
 */
public interface AmazonS3ClientService {
    String uploadFileToS3Bucket(File file, boolean enablePublicReadAccess) throws CustomException;

}
