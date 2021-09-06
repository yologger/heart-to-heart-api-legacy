package com.yologger.heart_to_heart_springboot.controller.test.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TestUserDTO {
    private final String name;
    private final String nation;
}
