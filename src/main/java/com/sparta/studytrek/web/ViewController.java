package com.sparta.studytrek.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/api/auth")
    public String showAuthPage() {
        return "login"; // 로그인&회원가입
    }
}
