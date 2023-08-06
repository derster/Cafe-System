package com.derster.cafesystem.controller;

import com.derster.cafesystem.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/user")
@CrossOrigin("*")
public interface UserController {
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping("/get")
    public ResponseEntity<List<UserWrapper>> getAll();

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping("/checkToken")
    ResponseEntity<String> checkToken();
    @PostMapping("/changePassword")
    ResponseEntity<String> changePassword(@RequestBody(required = true) Map<String, String> requestMap);
    @PostMapping("/forgot_password")
    ResponseEntity<String> forgotPassword(@RequestBody(required = true) Map<String, String> requestMap);
}
