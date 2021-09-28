package com.yologger.heart_to_heart_springboot.security.handler;

import com.yologger.heart_to_heart_springboot.security.exception.InvalidPasswordException;
import com.yologger.heart_to_heart_springboot.security.exception.MemberDoesNotExistException;
import lombok.extern.log4j.Log4j2;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Global exception handler.
@ControllerAdvice
@Log4j2
public class AuthExceptionHandler {

    @ExceptionHandler(MemberDoesNotExistException.class)
    public ResponseEntity<JSObject> handleMemberDoesNotExistException(MemberDoesNotExistException exception) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        JSONObject responseBody = new JSONObject();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("code", -3);
        responseBody.put("error", exception.getLocalizedMessage());

        return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<JSObject> handleInvalidPasswordException(InvalidPasswordException exception) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        JSONObject responseBody = new JSONObject();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("code", -4);
        responseBody.put("error", exception.getLocalizedMessage());

        return new ResponseEntity(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
    }
}
