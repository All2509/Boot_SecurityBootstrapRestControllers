package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) { // Изменено на поиск по email
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(Principal principal) {
        return findByEmail(principal.getName()); // Получаем из БД аутентифицированного пользователя по email
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // Изменено на использование email
        User user = findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", email));
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
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
    public User saveUserWithRoles(User user, Set<String> selectedRoles) {
        Set<Role> roles = selectedRoles.stream()
                .map(roleService::findByName)
                .collect(Collectors.toSet());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.createUser(user);
    }

    @Override
    @Transactional
    public User createUserFromParams(String name, String username, int age, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }



    @Override
    @Transactional
    public void createUser(User user) {
        userRepository.createUser(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userRepository.updateUser(user);
    }


    @Override
    @Transactional
    public void updateUserWithRoles(Long id, User user, Set<String> selectedRoles) {
        User existingUser = findUserById(id);
        if (existingUser != null) {
            existingUser.update(user.getName(), user.getAge(), user.getUsername(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getEmail(), null);

            Set<Role> roles = new HashSet<>();
            for (String roleName : selectedRoles) {
                Role role = roleService.findByName(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }
            existingUser.setRoles(roles);
            userRepository.updateUser(existingUser);
        }
    }
}