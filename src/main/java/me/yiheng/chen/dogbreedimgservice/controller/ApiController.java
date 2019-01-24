package me.yiheng.chen.dogbreedimgservice.controller;

import me.yiheng.chen.dogbreedimgservice.dto.DogBreedResponseDto;
import me.yiheng.chen.dogbreedimgservice.exception.CustomException;
import me.yiheng.chen.dogbreedimgservice.service.DogBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yiheng Chen
 * @date 20/1/19 12:48 PM
 */
@RestController
@RequestMapping("/api/v1/dogbreed")
public class ApiController {



    @Autowired
    private DogBreedService dogBreedService;

    @RequestMapping(method = RequestMethod.GET)
    public DogBreedResponseDto generateDogBreed() throws CustomException {
        DogBreedResponseDto responseDto = dogBreedService.generateDogBreedImg();

        return responseDto;
    }

}
