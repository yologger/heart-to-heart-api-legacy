package com.yologger.heart_to_heart_springboot.controller.api.v1.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UploadAvatarRequestDTO {
    private MultipartFile file;
    private Long userId;
}
