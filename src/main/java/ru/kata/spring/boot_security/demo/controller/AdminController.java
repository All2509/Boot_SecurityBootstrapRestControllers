package ru.kata.spring.boot_security.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

@RestController
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


    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.findAllRoles();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam int age,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam List<String> selectedRoles // список ролей как строки, например "ROLE_ADMIN"
    ) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(password);

        // Передача объекта User и ролей в сервис для сохранения
        userService.saveUserWithRoles(user, new HashSet<>(selectedRoles));

        return ResponseEntity.ok("User created");
    }




    @GetMapping("/delete_user")
    public ResponseEntity<Void> deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/edit")
    @ResponseBody
    public ResponseEntity<User> editUser(@RequestParam Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/edit")
    public ResponseEntity<User> setEdit(@RequestParam Long id,
                                        @ModelAttribute User user,
                                        @RequestParam Set<String> selectedRoles) {
        userService.updateUserWithRoles(id, user, selectedRoles);
        return ResponseEntity.ok(user);
    }
}
