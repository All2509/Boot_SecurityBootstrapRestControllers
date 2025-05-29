package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // Метод для сохранения нового пользователя
    public User createUser(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("User ID must be null for a new user.");
        }
        entityManager.persist(user);
        return user;
    }

    // Метод для обновления существующего пользователя
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID must not be null for an existing user.");
        }
        return entityManager.merge(user);
    }

    @Override
    public User findUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findByEmail(String email) { // Изменено на поиск по email
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        User user = findUserById(id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}