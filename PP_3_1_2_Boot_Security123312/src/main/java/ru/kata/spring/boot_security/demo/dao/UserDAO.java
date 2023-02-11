package ru.kata.spring.boot_security.demo.dao;

import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;


import java.util.List;
import java.util.Set;

public interface UserDAO {
    List<User> allUsers();


    @Transactional
    void save(User user);

    @Transactional
    void update(User user);

    void delete(User user);
    User getById(Long id);
    User getUserByName(String username);

}
