package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;


import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Transactional
public class UserDAOImpl implements UserDAO {
    public UserDAOImpl() {
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> allUsers() {
        List<User> resultList = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        return resultList;
    }

    @Override
    @Transactional
    public void save(User user) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }
        // добавление роли "admin" для первого пользователя
        if (allUsers().isEmpty()) {
            Role adminRole = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = 'admin'", Role.class)
                    .getResultList().stream().findFirst()
                    .orElseGet(() -> {
                        Role newAdminRole = new Role("admin");
                        entityManager.persist(newAdminRole);
                        return newAdminRole;
                    });
            roles.add(adminRole);
        }
        // добавление роли "user" для всех пользователей
        Role userRole = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = 'user'", Role.class)
                .getResultList().stream().findFirst()
                .orElseGet(() -> {
                    Role newUserRole = new Role("user");
                    entityManager.persist(newUserRole);
                    return newUserRole;
                });
        roles.add(userRole);
        user.setRoles(roles);
        entityManager.merge(user);
    }

    @Transactional
    @Override
    public void update(User user) {
        User existingUser = entityManager.find(User.class, user.getId());
        existingUser.setName(user.getName());
        existingUser.setAge(user.getAge());
        existingUser.setRoles(user.getRoles());
        entityManager.merge(existingUser);
    }

    @Transactional
    @Override
    public void delete(User user) {
        User managed = entityManager.merge(user);
        managed.getRoles().clear();
        entityManager.remove(managed);
    }

    @Override
    public User getById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User getUserByName(String username) {
        try {
            User user = entityManager.createQuery("SELECT u FROM User u where u.name = :name", User.class)
                    .setParameter("name", username)
                    .getSingleResult();
            return user;
        } catch (NoResultException ex) {
            return null;
        }
    }

}