package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ru.kata.spring.boot_security.demo.service.UserService;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;
    private final UserService userService;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserService userService) {
        this.successUserHandler = successUserHandler;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(new PasswordEncoderConfig().passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }
}
/*
*  Здесь вы создаете бин DaoAuthenticationProvider, который является провайдером аутентификации, использующим
* данные из базы данных. Вы устанавливаете PasswordEncoder с помощью метода setPasswordEncoder(). Это позволяет
* провайдеру правильно сравнивать закодированные пароли. Вы устанавливаете UserDetailsService с помощью метода
*  setUserDetailsService(userService). Это связывает провайдер аутентификации с вашим сервисом пользователей,
*  который будет использоваться для загрузки данных о пользователе. Процесс аутентификации:
Когда пользователь вводит свои учетные данные (логин и пароль), Spring Security вызывает метод loadUserByUsername()
* вашего userService, чтобы найти пользователя по введенному логину.
Если пользователь найден, возвращается объект UserDetails, содержащий информацию о пользователе
*  (например, имя пользователя, закодированный пароль и роли).
Провайдер аутентификации использует установленный PasswordEncoder, чтобы сравнить введенный пароль
*  с закодированным паролем из объекта UserDetails. Если пароли совпадают, аутентификация считается успешной.
Возврат провайдера:
В конце метода вы возвращаете созданный экземпляр authenticationProvider. Этот провайдер будет использоваться
*  Spring Security для обработки аутентификации пользователей в вашем приложении.
* */





