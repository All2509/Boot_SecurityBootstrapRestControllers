package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;
import java.security.Principal;
import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    List<User> findAll();
    User findUserById(Long id);
    void deleteById(Long id);
    User getCurrentUser(Principal principal);
    User saveUserWithRoles(User user, Set<String> selectedRoles);
    void save(User user);
    void updateUserWithRoles(Long id, User user, Set<String> selectedRoles);
}

/*
*     public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
      Метод loadUserByUsername: Этот метод принимает строку username, которая представляет имя пользователя,
*  и возвращает объект, реализующий интерфейс UserDetails.
* */
