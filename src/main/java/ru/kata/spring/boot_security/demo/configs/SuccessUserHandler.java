package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.core.Authentication; //Интерфейс, представляющий аутентификационные данные пользователя, включая его роли и права доступа.
import org.springframework.security.core.authority.AuthorityUtils; //класс, который предоставляет методы для работы с правами доступа (authorities).
import org.springframework.security.web.authentication.AuthenticationSuccessHandler; //Интерфейс, который определяет метод для обработки успешной аутентификации.
import org.springframework.stereotype.Component; // Аннотация, которая указывает, что данный класс является компонентом Spring
import javax.servlet.http.HttpServletRequest;  //Классы, представляющие HTTP-запросы
import javax.servlet.http.HttpServletResponse; //Классы, представляющие HTTP-ответы
import java.io.IOException;  //исключения
import java.util.Set;// не допускает дубликатов, коллекция

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {
    // Spring Security использует объект Authentication, пользователя авторизованной сессии.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) httpServletResponse.sendRedirect("/admin");
        else if (roles.contains("ROLE_USER")) {
            httpServletResponse.sendRedirect("/user");
        } else {
            httpServletResponse.sendRedirect("/login");
        }
    }
}




/*  Метод onAuthenticationSuccess: Этот метод вызывается после успешной аутентификации пользователя.
*  Он принимает три параметра. onAuthenticationSuccess это переопределенный метод интерфейса AuthenticationSuccessHandler
*  Здесь мы используем метод getAuthorities() объекта Authentication, чтобы получить список ролей (authorities) пользователя.
*  Затем мы преобразуем этот список в множество (Set) с помощью метода authorityListToSet.
*  Когда пользователь успешно проходит аутентификацию (например, вводит правильные учетные данные),
*  Spring Security вызывает метод onAuthenticationSuccess. В зависимости от роли пользователя он будет перенаправлен
*  либо на страницу /user, либо на главную страницу /.
*  Класс SuccessUserHandler является важной частью механизма аутентификации в приложении на основе Spring Security.
* Он позволяет управлять поведением приложения после успешной аутентификации пользователей в зависимости от их ролей.
*  Это помогает обеспечить правильный доступ к различным частям приложения в зависимости от прав пользователей
*/