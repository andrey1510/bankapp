package com.frontservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/")
public class RedirectController {

    @GetMapping
    public String redirectToMainPage() {
        return "redirect:/main";
    }

}
