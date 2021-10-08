package com.yologger.heart_to_heart_springboot.controller.api.v1.auth;

import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.JoinRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.LogInRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.TokenRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.service.AuthService;
import com.yologger.heart_to_heart_springboot.security.exception.InvalidPasswordException;
import com.yologger.heart_to_heart_springboot.security.exception.MemberDoesNotExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<JSObject> join(@RequestBody JoinRequestDTO request) {
        return authService.join(request);
    }

    @PostMapping("/login")
    public ResponseEntity<JSObject> logIn(@RequestBody LogInRequestDTO request) throws InvalidPasswordException, MemberDoesNotExistException {
        return authService.logIn(request);
    }

    @PostMapping("/token")
    public ResponseEntity<JSObject> token(@RequestBody TokenRequestDTO request) throws InvalidPasswordException, MemberDoesNotExistException {
        return authService.token(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<JSObject> logout(@RequestHeader(value="Authorization") String authHeader) {
        return authService.logout(authHeader);
    }

    @GetMapping("/verify")
    public ResponseEntity<JSObject> verify() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        JSONObject responseBody = new JSONObject();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("code", 1);
        responseBody.put("message", "Valid access token");

        return new ResponseEntity(responseBody, responseHeaders, HttpStatus.CREATED);
    }
}
