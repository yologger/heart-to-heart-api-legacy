package com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JoinRequestDTO {

    private String email;

    @JsonProperty(value = "full_name")
    private String fullName;

    private String nickname;

    private String password;

}
