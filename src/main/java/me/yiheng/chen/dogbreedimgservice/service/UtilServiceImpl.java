package me.yiheng.chen.dogbreedimgservice.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.yiheng.chen.dogbreedimgservice.exception.CustomException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static java.lang.String.format;

/**
 * @author Yiheng Chen
 * @date 21/1/19 6:39 PM
 */
@Service
@Slf4j
public class UtilServiceImpl implements UtilService {

    private static final String SUFFIX = ".jpg";

    @Override
    public File generateFileFromUrl(@NonNull String urlString, @NonNull String fileName) throws CustomException {

        File file;

        try {
            final URL url = new URL(urlString);
            final BufferedImage img = ImageIO.read(url);
            file = new File(fileName + SUFFIX);
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(format("exception writing file from url: {%s}, file name: {%s}", urlString, fileName));
        }
        return file;
    }

    @Override
    public MultipartFile convertFileToMultipartFile(File file) throws CustomException {
        try {
            DiskFileItem fileItem = new DiskFileItem(file.getName(), ContentType.IMAGE_JPEG.getMimeType(), false, file.getName(), (int) file.length() , file.getParentFile());
            fileItem.getOutputStream();
            return new CommonsMultipartFile(fileItem);
        } catch (IOException e) {
            log.error("exception converting file to MultipartFile");
            throw new CustomException("exception converting file to MultipartFile");
        }

    }
}
