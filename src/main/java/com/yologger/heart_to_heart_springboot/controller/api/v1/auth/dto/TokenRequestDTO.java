package com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@Data
public class TokenRequestDTO {

    @JsonProperty(value = "user_id")
    private Long id;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;
}