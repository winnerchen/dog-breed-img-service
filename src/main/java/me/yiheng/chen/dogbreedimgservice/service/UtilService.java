package me.yiheng.chen.dogbreedimgservice.service;

import me.yiheng.chen.dogbreedimgservice.exception.CustomException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author Yiheng Chen
 * @date 21/1/19 6:38 PM
 */
public interface UtilService {

    File generateFileFromUrl(String url, String fileName) throws CustomException;

    MultipartFile convertFileToMultipartFile(File file) throws CustomException;
}
