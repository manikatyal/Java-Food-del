package com.fooddel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    /** Health check used by Render and uptime pingers. */
    @GetMapping("/")
    public String root() {
        return "API working";
    }
}
