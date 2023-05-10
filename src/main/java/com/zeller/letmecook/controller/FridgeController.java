package com.zeller.letmecook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fridge")
public class FridgeController {

    @GetMapping("/")
    public String getHello() {
        return "hello from application";
    }
}

