package com.yologger.heart_to_heart_springboot.controller.test;

import com.yologger.heart_to_heart_springboot.controller.test.dto.TestUserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api")
public class TestApiController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }


    @GetMapping("/test1")
    public TestUserDTO test1(@RequestParam("name") String name, @RequestParam String nation) {
        return new TestUserDTO(name, nation);
    }
}
