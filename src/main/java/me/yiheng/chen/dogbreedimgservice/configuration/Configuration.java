package me.yiheng.chen.dogbreedimgservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author Yiheng Chen
 * @date 20/1/19 1:39 PM
 */
@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
