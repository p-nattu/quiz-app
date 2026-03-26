package com.example.quiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * トップ画面のコントローラー
 */
@Controller
public class TopController {

    /**
     * トップ画面を表示
     */
    @GetMapping("/")
    public String showTop() {
        return "top";
    }
}