package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
       // model.addAttribute("user", user); //добавляю сущность user со всеми полями
        model.addAttribute("currentUser", user);
        return "user";
    }
}
/*Здесь мы используем метод getName() объекта Principal, чтобы получить имя текущего аутентифицированного пользователя.
Затем мы вызываем метод findByUsername() сервиса userService, чтобы получить объект пользователя из базы данных по его имени.*/


