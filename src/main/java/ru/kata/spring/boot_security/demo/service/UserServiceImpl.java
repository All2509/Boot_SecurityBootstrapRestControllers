package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ru.kata.spring.boot_security.demo.model.Role;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(Principal principal) {
        return findByUsername(principal.getName()); // получаем из БД аутентифицированного пользователя
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }
   @Override
   @Transactional
    public void saveUser(String username, Integer age, String name, String password, String email, Set<Role> roles) {
        User user = new User (username, age, name, passwordEncoder.encode(password), email, roles);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional (readOnly = true)
    @Override
    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void updateUser(Long id, String name, Integer age, String username, String password, String email, Set<Role> roles) {
        User user = findUserById(id);
        if (user!= null) {
            // Создаем временный объект с новыми данными
            User updatedUser = new User(username, age, name, passwordEncoder.encode(password), email, roles);

            // Обновляем существующий объект
            user.update(name, age, username, updatedUser.getPassword(), email, roles);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void saveUserWithRoles(User user, Set<String> selectedRoles) {
        // Хэшируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();

        if (selectedRoles != null && !selectedRoles.isEmpty()) {
            for (String roleName : selectedRoles) {
                Role role = roleRepository.findByName(roleName); // Находим роль по имени
                if (role != null) {
                    roles.add(role); // Добавляем роль в набор, если она найдена
                }
            }
        }

        user.setRoles(roles); // Устанавливаем роли для пользователя
        userRepository.save(user); // Сохраняем пользователя в базе данных
    }

    public void updateUserWithRoles(Long id, User user, Set<String> selectedRoles) {
        // Получаем все роли из репозитория
        Set<Role> roles = new HashSet<>();
        for (String roleName : selectedRoles) {
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                roles.add(role);
            }
        }
        // Обновляем пользователя
        updateUser(id, user.getName(), user.getAge(), user.getUsername(), user.getPassword(), user.getEmail(), roles);
    }
}


