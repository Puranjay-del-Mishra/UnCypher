package com.DrawGPT.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {
    @GetMapping("/")
    public String sayHello() {
        return "Hello, DrawGPT!";
    }
}






