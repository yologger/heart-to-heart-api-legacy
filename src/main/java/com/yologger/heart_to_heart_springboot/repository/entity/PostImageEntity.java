package com.yologger.heart_to_heart_springboot.repository.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name= "post_image")
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostImageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String imageUrl;
}
