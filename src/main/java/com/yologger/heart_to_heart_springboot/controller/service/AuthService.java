package com.yologger.heart_to_heart_springboot.controller.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.JoinRequestDTO;
import com.yologger.heart_to_heart_springboot.repository.MemberRepository;
import netscape.javascript.JSObject;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    public ResponseEntity<JSObject> join(JoinRequestDTO request);
    public String logIn();

}
