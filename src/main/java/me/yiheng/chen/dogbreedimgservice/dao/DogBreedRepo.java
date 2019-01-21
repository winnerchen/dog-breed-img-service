package me.yiheng.chen.dogbreedimgservice.dao;

import me.yiheng.chen.dogbreedimgservice.domain.DogBreed;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Yiheng Chen
 * @date 20/1/19 1:30 PM
 */

public interface DogBreedRepo extends JpaRepository<DogBreed,Long> {
}
