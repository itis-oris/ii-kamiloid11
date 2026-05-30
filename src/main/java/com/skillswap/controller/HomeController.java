package com.skillswap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Корневой маршрут редиректит на каталог объявлений — он же главная страница MVP. */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/offers";
    }
}
