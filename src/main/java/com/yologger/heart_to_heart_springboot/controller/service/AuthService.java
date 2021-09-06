package com.yologger.heart_to_heart_springboot.controller.service;

import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.JoinRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.LogInRequestDTO;
import com.yologger.heart_to_heart_springboot.controller.api.v1.auth.dto.TokenRequestDTO;
import com.yologger.heart_to_heart_springboot.security.exception.InvalidPasswordException;
import com.yologger.heart_to_heart_springboot.security.exception.MemberDoesNotExistException;
import netscape.javascript.JSObject;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<JSObject> join(JoinRequestDTO request);
    ResponseEntity<JSObject> logIn(LogInRequestDTO request) throws MemberDoesNotExistException, InvalidPasswordException;
    ResponseEntity<JSObject> token(TokenRequestDTO request);
}
