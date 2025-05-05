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
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping()
    public String admin(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal); //получаем аутентифицированного пользователя из сессии
        model.addAttribute("currentUser", user); //добавляем в шаблон конкретного пользователя
        List<User> users = userService.findAll();    //находим всех пользователе
        model.addAttribute("users", users);   //добавляем их в шаблон
        return "admin";
    }

    @GetMapping("/new_user")
    public ModelAndView newUser(@ModelAttribute("user") User user) {  //ModelAndView — это контейнер, объединяющий данные модели и представление (шаблон, который будет использоваться для отображения этих данных).
        ModelAndView mav = new ModelAndView("new_user");  //new_user имя шаблона
        List<Role> roles = roleRepository.findAll();
        mav.addObject("allRoles", roles);  //можно добавлять данные в модель с помощью метода addObject()
        return mav; //не используется в данном шаблоне allRoles загружаются не динамически, а прописаны заранее
    }

    @PostMapping("/new_user")  //метод обрабатывает создание нового пользователя и связывает его с выбранными ролями
    public String saveUser(@ModelAttribute User user, //В этом коде аннотация @ModelAttribute("user") указывает Spring взять данные из запроса и заполнить ими объект типа User.
                           @RequestParam Set<String> selectedRoles) { //мы извлекаем набор строковых значений (имен ролей), которые были выбраны пользователем в форме(браузере).
        userService.saveUserWithRoles(user, selectedRoles); // Передаем пользователя и выбранные роли в сервис
        return "redirect:/admin";
    }

    @GetMapping("/delete_user")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String editUser(@RequestParam Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id));
        return "edit";
    }

    @PostMapping("/edit")
    public String setEdit(@RequestParam Long id,
                          @ModelAttribute User user,
                          @RequestParam Set<String> selectedRoles) {
        // Вызываем сервис для обновления пользователя с ролями
        userService.updateUserWithRoles(id, user, selectedRoles);
        return "redirect:/admin";
    }

    @PostMapping("/findByID")
    public String findByName(@RequestParam Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("currentUser", user);
        model.addAttribute("userFound", userService.findUserById(id));
        model.addAttribute("users", userService.findAll());
        return "admin";
    }
}
