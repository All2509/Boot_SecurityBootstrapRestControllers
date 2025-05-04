package ru.kata.spring.boot_security.demo.controller;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("currentUser", user);
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/admin/new_user")
    public ModelAndView newUser(@ModelAttribute("user") User user) {
        ModelAndView mav = new ModelAndView("new_user");
        List<Role> roles = roleRepository.findAll();
        mav.addObject("allRoles", roles);
        return mav;
    }

    @PostMapping("/admin/new_user")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam Set<String> selectedRoles) {
        Set<Role> roles = new HashSet<>();
        if (!selectedRoles.isEmpty()) {
            Arrays.stream(selectedRoles.toArray()).forEach(roleName -> roles.add(roleRepository.findByName(roleName.toString())));
        }
        user.setRoles(roles);
        userService.saveUser(user.getName(), user.getAge(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRoles());
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete_user")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit")
    public String editUser(@RequestParam Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id));
        return "edit";
    }

    @PostMapping("/admin/edit")
    public String setEdit(@RequestParam Long id, @ModelAttribute User user) {
        userService.updateUser(id, user.getName(), user.getAge(), user.getUsername(), user.getPassword(), user.getEmail());
        return "redirect:/admin";
    }

    @PostMapping("/admin/findByID")
    public String findByName(@RequestParam Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("currentUser", user);
        model.addAttribute("userFound", userService.findUserById(id));
        model.addAttribute("users", userService.findAll());
        return "admin";
    }
}
