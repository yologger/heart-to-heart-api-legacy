package com.yologger.heart_to_heart_springboot.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.JoinRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.service.AuthService;
import com.yologger.heart_to_heart_springboot.repository.MemberRepository;
import com.yologger.heart_to_heart_springboot.repository.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public ResponseEntity<JSObject> join(JoinRequestDTO request) {

        String email = request.getEmail();
        String fullName = request.getFullName();
        String nickname = request.getNickname();
        String password = request.getPassword();

        if (email == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("code", -1);
            responseBody.put("error", "'email' field must not be empty.");

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        if (fullName == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("code", -2);
            responseBody.put("error", "'full_name' field must not be empty.");

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        if (nickname == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("code", -3);
            responseBody.put("error", "'nickname' field must not be empty.");

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        if (password == null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("code", -4);
            responseBody.put("error", "'password' field must not be empty.");

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        // Check if member already exists.
        Optional<MemberEntity> result = memberRepository.findByEmail(email);

        // In case member already exists.
        if (result.isPresent()) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("code", -5);
            responseBody.put("error", "Member already exists.");

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }


        MemberEntity newMember = MemberEntity.builder()
                .email(email)
                .fullName(fullName)
                .nickname(nickname)
                .password(password)
                .build();

        try {
            memberRepository.save(newMember);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject data = new JSONObject();
            data.put("email", email);
            data.put("full_name", fullName);
            data.put("nickname", nickname);

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.CREATED.value());
            responseBody.put("code", 1);
            responseBody.put("message", "Successfully joined.");
            responseBody.put("data", data);

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            JSONObject responseBody = new JSONObject();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("code", -6);
            responseBody.put("error", e.getLocalizedMessage());

            return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public String logIn() {
        return "";
    }
}
