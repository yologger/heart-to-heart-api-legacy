package com.yologger.heart_to_heart_springboot.controller.api.v1.member;

import com.yologger.heart_to_heart_springboot.controller.api.v1.member.dto.UploadAvatarRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getMemberById(@PathVariable("id") Long memberId) {
        return memberService.getMemberById(memberId);
    }

    @PostMapping(value = "/avatar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<JSONObject> uploadAvatar(
            @RequestPart(value = "file", required = true) MultipartFile file,
            @RequestParam("user_id") Long userId
    ) {
        UploadAvatarRequestDTO request = UploadAvatarRequestDTO.builder()
                .file(file)
                .userId(userId)
                .build();

        return memberService.uploadAvatar(request);
    }
}
