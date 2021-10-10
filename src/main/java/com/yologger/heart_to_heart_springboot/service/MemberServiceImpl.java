package com.yologger.heart_to_heart_springboot.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.member.dto.UploadAvatarRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.service.MemberService;
import com.yologger.heart_to_heart_springboot.repository.MemberRepository;
import com.yologger.heart_to_heart_springboot.repository.entity.MemberEntity;
import com.yologger.heart_to_heart_springboot.util.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AwsS3Uploader awsS3Uploader;

    @Transactional
    @Override
    public ResponseEntity<JSONObject> getMemberById(Long memberId) {

        if (memberId == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("code", -1);
            responseBody.put("error", "path variable 'id' must not be empty.");

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        try {
            MemberEntity member = memberRepository.getById(memberId);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject data = new JSONObject();
            data.put("email", member.getEmail());
            data.put("full_name", member.getFullName());
            data.put("nickname", member.getNickname());
            data.put("avatar_url", member.getAvatarUrl());

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("code", 1);
            responseBody.put("data", data);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();

            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("code", -2);
            responseBody.put("error", "Member with id " + memberId + " does not exits.");

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<JSONObject> uploadAvatar(UploadAvatarRequestDTO request) {

        Long userId = request.getUserId();

        if (userId == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "'user_id' field must not empty");
            responseBody.put("code", -1);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }


        // Check if member already exists.
        Optional<MemberEntity> result = memberRepository.findById(userId);

        if (result.isEmpty()) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "User does not exist.");
            responseBody.put("code", -2);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        MultipartFile file = request.getFile();

        if (file.isEmpty()) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "File is empty.");
            responseBody.put("code", -3);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        // Check if content-type is image
        if (!file.getContentType().startsWith("image")) {

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", "'Content-type' is invalid. 'Content-type' must be image.");
            responseBody.put("code", -4);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        try {
            // Upload file
            String imageUrl = awsS3Uploader.upload(file);
            MemberEntity member = result.get();
            member.setAvatarUrl(imageUrl);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject data = new JSONObject();
            data.put("user_id", member.getId());
            data.put("image_url", member.getAvatarUrl());

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.CREATED.value());
            responseBody.put("message", "Successfully posted.");
            responseBody.put("data", data);
            responseBody.put("code", 1);
            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.CREATED);

        } catch (Exception e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("error", e.getLocalizedMessage());
            responseBody.put("code", -5);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }
}
