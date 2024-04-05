package com.main.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("api/register")

public class RegisterController {

    @PostMapping("/administrator")
    public String postMethodName(@RequestParam String password) {
        return password;
    }
}