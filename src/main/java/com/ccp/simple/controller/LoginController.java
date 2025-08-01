package com.ccp.simple.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String index() {
        return "redirect:/login.html";
    }
}
