package com.yologger.heart_to_heart_springboot.repository.exception;

public class MemberDoesNotExistException extends Exception {
    public MemberDoesNotExistException(String message) { super(message); }
}
