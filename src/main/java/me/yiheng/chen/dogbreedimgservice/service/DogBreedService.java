package me.yiheng.chen.dogbreedimgservice.service;

import me.yiheng.chen.dogbreedimgservice.dto.DogBreedResponseDto;
import me.yiheng.chen.dogbreedimgservice.exception.CustomException;

import java.util.List;

/**
 * @author Yiheng Chen
 * @date 20/1/19 1:18 PM
 */
public interface DogBreedService {

    String STATUS_SUCCESS = "SUCCESS";

    String IMAGE_FORMAT_SUFFIX = ".jpg";

    /**
     * generate a new dog breed record
     */
    DogBreedResponseDto generateDogBreedImg() throws CustomException;

    /**
     * retrieve the record from the database with the given id.
     * @param id
     * @return DogBreedResponseDto
     */
    DogBreedResponseDto retrieveById(Long id);

    /**
     * remove the record from the database with the given id.
     * @param id
     * @return String successful/fail
     */
    DogBreedResponseDto removeById(Long id) throws CustomException;

    /**
     * retrieve any records from the database with the given dog breed name
     * @param name
     * @return list of dog breed
     */
    List<DogBreedResponseDto> searchByName(String name);

    /**
     * retrieve a list of dog breeds stored in the system.
     * @return list of names
     */
    List<String> findBreedNames();
}
