package com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class LogInRequestDTO {
    private String email;
    private String password;
}
