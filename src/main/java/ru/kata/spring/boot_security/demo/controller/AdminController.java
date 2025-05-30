package ru.kata.spring.boot_security.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.stereotype.Controller;
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
    public ModelAndView admin(Principal principal) {
        ModelAndView mav = new ModelAndView("admin");
        User user = userService.getCurrentUser(principal);
        List<User> users = userService.findAll();
        mav.addObject("currentUser", user);
        mav.addObject("users", users);
        return mav;
    }

    @GetMapping("/new_user")
    public ModelAndView newUser(@ModelAttribute("user") User user) {
        ModelAndView mav = new ModelAndView("new_user");
        List<Role> roles = roleService.findAllRoles();
        mav.addObject("allRoles", roles);
        return mav;
    }

    @PostMapping("/new_user")
    public ModelAndView saveUser(@ModelAttribute User user,
                                 @RequestParam Set<String> selectedRoles) {
        userService.saveUserWithRoles(user, selectedRoles);
        return new ModelAndView("redirect:/admin");
    }


    @GetMapping("/edit")
    public ModelAndView editUser(@RequestParam Long id) {
        User user = userService.findUserById(id);
        ModelAndView mav = new ModelAndView("admin");
        if (user != null) {
            mav.addObject("user", user);
        }
        return mav;
    }

    @PostMapping("/edit")
    public ModelAndView setEdit(@RequestParam Long id,
                          @ModelAttribute User user,
                          @RequestParam Set<String> selectedRoles) {
        userService.updateUserWithRoles(id, user, selectedRoles);
        return new ModelAndView("redirect:/admin");
    }
}