package me.yiheng.chen.dogbreedimgservice.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yiheng Chen
 * @date 20/1/19 1:03 PM
 */
@Data
@JsonAutoDetect
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DogBreedResponseDto {

    private String status;

    private String resourceUrl;

}
