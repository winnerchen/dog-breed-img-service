package me.yiheng.chen.dogbreedimgservice.controller;

import me.yiheng.chen.dogbreedimgservice.dto.DogBreedResponseDto;
import me.yiheng.chen.dogbreedimgservice.exception.CustomException;
import me.yiheng.chen.dogbreedimgservice.service.DogBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * @author Yiheng Chen
 * @date 20/1/19 12:48 PM
 */
@RestController
@RequestMapping("/api/v1/dogbreed")
public class ApiController {


    @Autowired
    private DogBreedService dogBreedService;

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public DogBreedResponseDto generateDogBreed() throws CustomException {
        DogBreedResponseDto responseDto = dogBreedService.generateDogBreedImg();

        return responseDto;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<DogBreedResponseDto> retrieveDogBreedById(@PathVariable Long id) throws CustomException {
        DogBreedResponseDto responseDto = dogBreedService.retrieveById(id);

        return responseDto.getId() != null ? new ResponseEntity<>(responseDto, HttpStatus.OK) : new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<DogBreedResponseDto> removeDogBreedById(@PathVariable Long id) throws CustomException {
        DogBreedResponseDto responseDto = dogBreedService.removeById(id);

        return responseDto.getId() != null ? new ResponseEntity<>(responseDto, HttpStatus.OK) : new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/name/{name}",method = RequestMethod.GET)
    public ResponseEntity<List<DogBreedResponseDto>> retrieveDogBreedByName(@PathVariable String name) throws CustomException {
        List<DogBreedResponseDto> dogBreedResponseDtos = dogBreedService.searchByName(name);

        if (dogBreedResponseDtos == null) {
            List<DogBreedResponseDto> responseList = Arrays.asList(DogBreedResponseDto.builder().message(format("The resource with name = {%s} does not exist", name)).build());

            return new ResponseEntity<>(responseList, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dogBreedResponseDtos, HttpStatus.OK);
    }






}
