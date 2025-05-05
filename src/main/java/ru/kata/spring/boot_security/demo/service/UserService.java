package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.security.Principal;
import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    void saveUser(String name, Integer age, String username, String password, String email, Set<Role> roles);
    List<User> findAll();
    User findUserById(Long id);
    void deleteById(Long id);
    void updateUser(Long id, String name,Integer age ,String username, String password, String email, Set<Role> roles);
    User getCurrentUser(Principal principal);
    void saveUserWithRoles(User user, Set<String> selectedRoles);
    void updateUserWithRoles(Long id, User user, Set<String> selectedRoles);
}

/*
*     public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
      Метод loadUserByUsername: Этот метод принимает строку username, которая представляет имя пользователя,
*  и возвращает объект, реализующий интерфейс UserDetails.
* */
