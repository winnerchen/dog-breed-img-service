package me.yiheng.chen.dogbreedimgservice.service;

import me.yiheng.chen.dogbreedimgservice.dao.DogBreedRepo;
import me.yiheng.chen.dogbreedimgservice.domain.DogBreed;
import me.yiheng.chen.dogbreedimgservice.dto.DogBreedResponseDto;
import me.yiheng.chen.dogbreedimgservice.dto.ExternalDogBreedResponseDto;
import me.yiheng.chen.dogbreedimgservice.exception.CustomException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Yiheng Chen
 * @date 29/1/19 10:57 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class DogBreedServiceImplTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    UtilService utilService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    AmazonS3ClientService amazonS3ClientService;

    @Mock
    DogBreedRepo dogBreedRepo;

    @InjectMocks
    DogBreedServiceImpl subject;

    @Test
    public void given_externalDogBreedResponseDtoOfNull_when_generateDogBreedImg_then_exception() throws CustomException {

        //given externalDogBreedResponseDto = null


        // then
        exception.expect(CustomException.class);
        exception.expectMessage("response from external dog breed api is null");

        //when
        subject.generateDogBreedImg();

    }

    @Test
    public void given_externalImageUrlOfNull_when_generateDogBreedImg_then_exception() throws CustomException {

        //given
        subject.setDogBreedApiUrl("fake_url");
        ExternalDogBreedResponseDto mockResponseDto = mock(ExternalDogBreedResponseDto.class);
        when(mockResponseDto.getMessage()).thenReturn(null);

        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(mockResponseDto);

        // then
        exception.expect(NullPointerException.class);

        //when
        subject.generateDogBreedImg();

    }

    @Test
    public void given_invalidExternalImageUrl_when_generateDogBreedImg_then_exception() throws CustomException {

        //given
        subject.setDogBreedApiUrl("fake_url");
        ExternalDogBreedResponseDto mockResponseDto = mock(ExternalDogBreedResponseDto.class);
        when(mockResponseDto.getMessage()).thenReturn("invalid_image_url");

        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(mockResponseDto);

        // then
        exception.expect(CustomException.class);
        exception.expectMessage("Response image url");

        //when
        subject.generateDogBreedImg();

    }

    @Test
    public void given_fileOfNull_when_generateDogBreedImg_then_exception() throws CustomException {

        //given
        String validUrl = "https://images.dog.ceo/breeds/redbone/n02090379_2126.jpg";

        subject.setDogBreedApiUrl("fake_url");
        ExternalDogBreedResponseDto mockResponseDto = mock(ExternalDogBreedResponseDto.class);
        when(mockResponseDto.getMessage()).thenReturn(validUrl);

        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(mockResponseDto);
        when(utilService.generateFileFromUrl(anyString(), anyString())).thenReturn(null);

        // then
        //exception.expect(NullPointerException.class);

        //when
        subject.generateDogBreedImg();

    }

    @Test
    public void given_allValidInputs_when_generateDogBreedImg_then_response() throws CustomException {

        //given
        String validUrl = "https://images.dog.ceo/breeds/redbone/n02090379_2126.jpg";
        String validExternalEndpoint = "https://dog.ceo/api/breeds/image/random";

        subject.setDogBreedApiUrl(validExternalEndpoint);
        ExternalDogBreedResponseDto mockResponseDto = mock(ExternalDogBreedResponseDto.class);
        when(mockResponseDto.getMessage()).thenReturn(validUrl);

        when(restTemplate.getForObject(validExternalEndpoint, ExternalDogBreedResponseDto.class)).thenReturn(mockResponseDto);
        when(utilService.generateFileFromUrl(anyString(), anyString())).thenReturn(mock(File.class));

        when(amazonS3ClientService.uploadFileToS3Bucket(any(File.class), anyBoolean())).thenReturn("my_resource_url");
        when(dogBreedRepo.save(any(DogBreed.class))).then(returnsFirstArg());


        //when
        DogBreedResponseDto responseDto = subject.generateDogBreedImg();

        // then
        assertEquals("my_resource_url", responseDto.getResourceUrl());
        assertEquals("SUCCESS", responseDto.getStatus());

    }


    @Test
    public void given_noResultForInputId_when_generateDogBreedImg_then_returnResourceNotFound() throws CustomException {
        //given
        when(dogBreedRepo.findById(anyLong())).thenReturn(Optional.empty());

        //when
        DogBreedResponseDto responseDto = subject.retrieveById(1L);

        //then
        assertThat(responseDto.getMessage(), containsString(" does not exist"));

    }

    @Test
    public void given_singleResultForInputId_when_generateDogBreedImg_then_returnResourceNotFound() throws CustomException, ParseException {
        //given
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = simpleDateFormat.parse("2018-09-09 00:00:00");


        DogBreed mockDogBreed = mock(DogBreed.class);
        when(mockDogBreed.getName()).thenReturn("dog_name");
        when(mockDogBreed.getId()).thenReturn(1L);
        when(mockDogBreed.getS3lImageUrl()).thenReturn("s3_image_url");
        //when(mockDogBreed.getExternalImageUrl()).thenReturn("external_image_url");
        when(mockDogBreed.getCreatedOn()).thenReturn(new Timestamp(date.getTime()));


        when(dogBreedRepo.findById(anyLong())).thenReturn(Optional.of(mockDogBreed));

        //when
        DogBreedResponseDto responseDto = subject.retrieveById(1L);

        //then
        assertThat(responseDto.getMessage(), is(nullValue()));
        assertThat(responseDto.getId(), is(1L));
        assertThat(responseDto.getStatus(), is("SUCCESS"));
        assertThat(responseDto.getName(), is("dog_name"));
        assertThat(responseDto.getResourceUrl(), is("s3_image_url"));
        assertThat(responseDto.getCreatedOn(), is(new Timestamp(date.getTime())));


    }

}