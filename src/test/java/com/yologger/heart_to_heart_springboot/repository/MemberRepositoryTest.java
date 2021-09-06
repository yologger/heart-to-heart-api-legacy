package com.yologger.heart_to_heart_springboot.repository;

import com.yologger.heart_to_heart_springboot.repository.MemberRepository;
import com.yologger.heart_to_heart_springboot.repository.entity.MemberEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    public void cleanup() {
        // memberRepository.deleteAll();
    }

    @Test
    public void savePosts() {
        memberRepository.save(MemberEntity.builder().email("ronaldo@gmail.com").fullName("Cristiano Ronaldo").nickname("CR7").password("1234").build());
        memberRepository.save(MemberEntity.builder().email("messi@gmail.com").fullName("Lionel Messi ").nickname("messi10").password("1234").build());
    }

    @Test
    public void FetchPosts() {
        List<MemberEntity> members = memberRepository.findAll();
        System.out.println("size: " + members.size());
    }
}
