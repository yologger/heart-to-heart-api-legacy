package com.yologger.heart_to_heart_springboot.controller.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestApiController.class)
public class TestApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void test() throws Exception {

        mvc.perform(get("/test/api/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    public void test1() throws Exception {
        String name = "Messi";
        String nation = "Argentina";

        mvc.perform(get("/test/api/test1")
                .param("name", name)
                .param("nation", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Ronaldo")));
    }
}
