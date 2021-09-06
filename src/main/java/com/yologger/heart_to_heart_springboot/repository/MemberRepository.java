package com.yologger.heart_to_heart_springboot.repository;

import com.yologger.heart_to_heart_springboot.repository.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    // Query Method
    Optional<MemberEntity> findByEmail(String email);
}
