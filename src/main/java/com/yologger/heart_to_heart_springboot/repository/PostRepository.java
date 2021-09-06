package com.yologger.heart_to_heart_springboot.repository;

import com.yologger.heart_to_heart_springboot.repository.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

}
