package com.yologger.heart_to_heart_springboot.controller.api.v1.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberDTO {

    private long id;
    private String email;
    private String fullName;
    private String nickname;
    private String password;
    private LocalDateTime createdAt, updatedAt;
}
