package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @GetMapping("/login")
    public ModelAndView showLoginPage() {
        return new ModelAndView("login");
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}