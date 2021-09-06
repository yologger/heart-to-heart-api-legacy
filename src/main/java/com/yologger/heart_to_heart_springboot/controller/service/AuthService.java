package com.yologger.heart_to_heart_springboot.controller.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.JoinRequestDTO;
import netscape.javascript.JSObject;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<JSObject> join(JoinRequestDTO request);
    String logIn();

}
