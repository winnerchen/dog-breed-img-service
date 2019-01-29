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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

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

    @Autowired
    private String awsS3Bucket;

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
    public DogBreedResponseDto retrieveById(Long id) {
        Optional<DogBreed> optionalDogBreed = dogBreedRepo.findById(Long.valueOf(id));

        if (!optionalDogBreed.isPresent()){
            return DogBreedResponseDto.builder().message(format("resource with the request id {%s} does not exist", id)).build();
        }

        DogBreed dogBreed = optionalDogBreed.get();
        return DogBreedResponseDto.builder()
                .id(dogBreed.getId())
                .name(dogBreed.getName())
                .resourceUrl(dogBreed.getS3lImageUrl())
                .createdOn(dogBreed.getCreatedOn())
                .build();
    }

    @Override
    public DogBreedResponseDto removeById(Long id) throws CustomException {
        Optional<DogBreed> optionalDogBreed = dogBreedRepo.findById(Long.valueOf(id));
        if (!optionalDogBreed.isPresent()){
            return DogBreedResponseDto.builder().message(format("the resource you try to remove with id {%s} does not exist", id)).build();
        }


        //remove record from database
        try{
            dogBreedRepo.deleteById(id);
        }catch (Exception e){
            throw new CustomException(format("Exception remove resource with id: {%s}", id));
        }
        //remove image from s3
        if (!amazonS3ClientService.deleteFileFromS3Bucket(awsS3Bucket, optionalDogBreed.get().getName())){
            throw new CustomException("Exception removing file from s3 bucket");
        }

        return DogBreedResponseDto.builder()
                .id(optionalDogBreed.get().getId())
                .message("Resource successfully removed")
                .build();
    }

    @Override
    public List<DogBreedResponseDto> searchByName(String name) {
        List<DogBreed> dogBreeds = dogBreedRepo.findByName(name);
        if (dogBreeds == null || dogBreeds.size() == 0) {
            return null;
        }

        List<DogBreedResponseDto> dtoList = new ArrayList<>();

        for (DogBreed dogBreed : dogBreeds) {
            dtoList.add(DogBreedResponseDto.builder().id(dogBreed.getId())
                    .name(dogBreed.getName())
                    .createdOn(dogBreed.getCreatedOn())
                    .resourceUrl(dogBreed.getS3lImageUrl()).build());
        }

        return dtoList;
    }

    @Override
    public List<String> findBreedNames() {
        return null;
    }

    private String getDogNameFromUrl(String url) {
        return url.split("/")[4];
    }


}
