package ru.kata.spring.boot_security.demo.repositories;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserRepository {
    User findByEmail(String email); // Изменено на поиск по email
    User findUserById(Long id);
    List<User> findAll();
    void deleteById(Long id);
    User createUser(User user);
    User updateUser(User user);
}
