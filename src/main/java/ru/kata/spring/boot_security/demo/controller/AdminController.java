package ru.kata.spring.boot_security.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String admin(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal); // Получаем аутентифицированного пользователя из сессии
        model.addAttribute("currentUser", user); // Добавляем в шаблон конкретного пользователя
        List<User> users = userService.findAll(); // Находим всех пользователей
        model.addAttribute("users", users); // Добавляем их в шаблон
        return "admin";
    }

    @GetMapping("/new_user")
    public ModelAndView newUser(@ModelAttribute("user") User user) {
        ModelAndView mav = new ModelAndView("new_user");
        List<Role> roles = roleService.findAllRoles();
        mav.addObject("allRoles", roles);
        return mav;
    }

    @PostMapping("/new_user")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam Set<String> selectedRoles) {
        User preparedUser = userService.saveUserWithRoles(user, selectedRoles);
        return "redirect:/admin";
    }


    @GetMapping("/delete_user")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

//    @GetMapping("/edit")
//    public String editUser(@RequestParam Long id, Model model) {
//        model.addAttribute("user", userService.findUserById(id));
//        return "edit";
//    }

    @GetMapping("/edit")
    @ResponseBody // Указывает, что результат будет возвращен как тело ответа
    public ResponseEntity<User> editUser(@RequestParam Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user); // Возвращаем пользователя с кодом 200 OK
        } else {
            return ResponseEntity.notFound().build(); // Возвращаем 404, если пользователь не найден
        }
    }

    @PostMapping("/edit")
    public String setEdit(@RequestParam Long id,
                          @ModelAttribute User user,
                          @RequestParam Set<String> selectedRoles) {
        userService.updateUserWithRoles(id, user, selectedRoles);
        return "redirect:/admin";
    }

    @PostMapping("/findByID")
    public String findByName(@RequestParam Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Изменяем метод поиска на поиск по email
        User user = userService.findByEmail(userDetails.getUsername());

        model.addAttribute("currentUser", user);
        model.addAttribute("userFound", userService.findUserById(id));
        model.addAttribute("users", userService.findAll());

        return "admin";
    }
}