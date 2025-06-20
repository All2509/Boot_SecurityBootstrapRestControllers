package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;
import java.security.Principal;
import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    User findByEmail(String email); // Изменено на поиск по email
    List<User> findAll();
    User findUserById(Long id);
    void deleteById(Long id);
    User getCurrentUser(Principal principal);
    User saveUserWithRoles(User user, Set<String> selectedRoles);
    void createUser(User user);
    void updateUserWithRoles(Long id, User user, Set<String> selectedRoles);
    void updateUser(User user);
    public User createUserFromParams(String name, String username, int age, String email, String password);
}

/*
*     public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
      Метод loadUserByUsername: Этот метод принимает строку username, которая представляет имя пользователя,
*  и возвращает объект, реализующий интерфейс UserDetails.
* */
