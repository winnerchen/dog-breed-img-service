package me.yiheng.chen.dogbreedimgservice.service;

import me.yiheng.chen.dogbreedimgservice.dao.DogBreedRepo;
import me.yiheng.chen.dogbreedimgservice.domain.DogBreed;
import me.yiheng.chen.dogbreedimgservice.dto.DogBreedResponseDto;
import me.yiheng.chen.dogbreedimgservice.dto.ExternalDogBreedResponseDto;
import me.yiheng.chen.dogbreedimgservice.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author Yiheng Chen
 * @date 20/1/19 1:29 PM
 */
@Service
public class DogBreedServiceImpl implements DogBreedService {

    @Value("${dogbreed.api.url}")
    private String dogBreedApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DogBreedRepo dogBreedRepo;

    @Autowired
    private UtilService utilService;

    @Autowired
    private AmazonS3ClientService amazonS3ClientService;

    @Override
    public DogBreedResponseDto generateDogBreedImg() throws CustomException {

        ExternalDogBreedResponseDto responseDto;

        try {
            responseDto = restTemplate.getForObject(dogBreedApiUrl, ExternalDogBreedResponseDto.class);
        } catch (Exception e) {
            throw new CustomException("Exception accessing external dog breed api");
        }

        Optional.ofNullable(responseDto).orElseThrow(() -> new CustomException("response from external dog breed api is null"));

        String externalImageUrl = responseDto.getMessage();

        String dogName = getDogNameFromUrl(externalImageUrl);

        //store image to s3 bucket
        File file = utilService.generateFileFromUrl(externalImageUrl, dogName);

        String resourceUrl = amazonS3ClientService.uploadFileToS3Bucket(file, true);

        DogBreed dogBreed = DogBreed.builder()
                .name(dogName)
                .externalImageUrl(externalImageUrl)
                .s3lImageUrl(resourceUrl)
                .build();

        //persist data to database
        dogBreedRepo.save(dogBreed);

        return DogBreedResponseDto.builder()
                .resourceUrl(resourceUrl)
                .status("success")
                .build();
    }

    @Override
    public DogBreedResponseDto retrieveById(String id) {
        return null;
    }

    @Override
    public String removeById(String id) {
        return null;
    }

    @Override
    public List<DogBreedResponseDto> searchByName(String name) {
        return null;
    }

    @Override
    public List<String> findBreedNames() {
        return null;
    }

    private String getDogNameFromUrl(String url) {
        return url.split("/")[4];
    }


}
