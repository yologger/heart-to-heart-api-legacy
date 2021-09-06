package com.yologger.heart_to_heart_springboot.controller.test;

import com.yologger.heart_to_heart_springboot.controller.test.dto.TestUserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api")
public class TestApiController {

    @GetMapping("/test1")
    public String test1() {
        return "test1";
    }

    @GetMapping("/test2")
    public String test2() {
        return "test2";
    }

    @GetMapping("/test3")
    public TestUserDTO test3(@RequestParam("name") String name, @RequestParam String nation) {
        return new TestUserDTO(name, nation);
    }


}
