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

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@Log4j2
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<JSONObject> register(@RequestPart(value = "files", required = false) MultipartFile[] files, @RequestParam("user_id") Long userId, @RequestParam("content") String content) {

        RegisterPostRequestDTO request = RegisterPostRequestDTO.builder()
                .files(files)
                .userId(userId)
                .content(content)
                .build();

        return postService.registerPost(request);
    }

    @GetMapping("/posts")
    public ResponseEntity<JSONObject> getPosts(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return postService.getPosts(page, size);
    }

    @GetMapping("/post/{post_id}")
    public ResponseEntity<JSONObject> getPost(@PathVariable("post_id") Long postId) {
        return postService.getPost(postId);
    }
}