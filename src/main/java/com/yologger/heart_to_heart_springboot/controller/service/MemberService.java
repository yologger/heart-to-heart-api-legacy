package com.yologger.heart_to_heart_springboot.controller.service;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    public ResponseEntity<JSONObject> getMemberById(Long memberId);
}
