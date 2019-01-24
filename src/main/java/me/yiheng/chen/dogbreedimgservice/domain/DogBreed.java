package me.yiheng.chen.dogbreedimgservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Yiheng Chen
 * @date 20/1/19 1:09 PM
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DogBreed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "updated_on")
    @UpdateTimestamp
    private Timestamp updatedOn;

    @Column(name = "external_image_url")
    private String externalImageUrl;

    @Column(name = "s3_image_url")
    private String s3lImageUrl;

}
