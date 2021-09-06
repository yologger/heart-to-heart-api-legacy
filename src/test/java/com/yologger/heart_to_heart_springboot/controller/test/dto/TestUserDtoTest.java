package com.yologger.heart_to_heart_springboot.controller.test.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TestUserDtoTest {

    @Test
    public void test1() {
        String name = "Ronaldo";
        String nation = "Portugal";

        TestUserDTO dto = new TestUserDTO(name, nation);

        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getNation()).isEqualTo(nation);
    }
}
