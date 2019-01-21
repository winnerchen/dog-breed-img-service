package me.yiheng.chen.dogbreedimgservice.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

/**
 * @author Yiheng Chen
 * @date 20/1/19 2:07 PM
 */
@Data
@JsonAutoDetect
public class ExternalDogBreedResponseDto {
    private String status;

    private String message;

}
