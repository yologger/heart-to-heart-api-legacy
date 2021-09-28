package com.yologger.heart_to_heart_springboot.controller.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.post.dto.RegisterPostRequestDTO;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

public interface PostService {

    ResponseEntity<JSONObject> registerPost(RegisterPostRequestDTO request);
    ResponseEntity<JSONObject> getPost(Long postId);
    ResponseEntity<JSONObject> getPosts(Integer page, Integer size);
}
