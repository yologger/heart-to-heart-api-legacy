package com.yologger.heart_to_heart_springboot.controller.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.post.dto.RegisterPostRequestDTO;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PostService {

    public ResponseEntity<JSONObject> registerPost(RegisterPostRequestDTO request);
}
