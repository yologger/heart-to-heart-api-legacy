package com.yologger.heart_to_heart_springboot.controller.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.member.dto.UploadAvatarRequestDTO;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    public ResponseEntity<JSONObject> getMemberById(Long memberId);
    public ResponseEntity<JSONObject> uploadAvatar(UploadAvatarRequestDTO request);
}
