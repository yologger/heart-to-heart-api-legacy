package com.yologger.heart_to_heart_springboot.controller.api.v1.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterPostRequestDTO {

    private MultipartFile[] files;

    private Long userId;

    private String content;
}
