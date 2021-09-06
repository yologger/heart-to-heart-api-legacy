package com.yologger.heart_to_heart_springboot.controller.api.v1.auth;

import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.JoinRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.LogInRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import netscape.javascript.JSObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String logIn(@RequestBody LogInRequestDTO request) {
        log.info("request: " + request);
        return "logIn";
    }
}
