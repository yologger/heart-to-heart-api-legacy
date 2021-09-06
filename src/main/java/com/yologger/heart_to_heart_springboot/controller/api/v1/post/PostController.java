package com.yologger.heart_to_heart_springboot.controller.api.v1.post;

import com.yologger.heart_to_heart_springboot.controller.api.v1.post.dto.RegisterPostRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@Log4j2
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<JSONObject> register(@RequestPart("files") MultipartFile[] files,
                                               @RequestParam("user_id") Long userId,
                                               @RequestParam("title") String title,
                                               @RequestParam("content") String content) {

        RegisterPostRequestDTO request = RegisterPostRequestDTO.builder()
                .files(files)
                .userId(userId)
                .title(title)
                .content(content)
                .build();

        return postService.registerPost(request);
    }
}
