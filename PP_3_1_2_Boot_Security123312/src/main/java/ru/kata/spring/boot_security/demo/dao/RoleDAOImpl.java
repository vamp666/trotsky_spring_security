package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Set;

@Component
@Transactional
public class RoleDAOImpl implements RoleDAO{
    @PersistenceContext
    private EntityManager entityManager;

    public RoleDAOImpl() {
    }

    @Transactional
    @Override
    public void save(Role role) {
        Role managed = entityManager.merge(role);
        entityManager.persist(managed);
    }

    @Transactional
    @Override
    public void delete(Role role) {
        Role managed = entityManager.merge(role);
        entityManager.remove(managed);
    }

    @Override
    public Role getById(Long id) {
        return entityManager.find(Role.class, id );
    }

    @Override
    public Role getRoleByName(String rolename) {
         Role role;
        try {
            role = entityManager.createQuery("SELECT r FROM Role r where r.name = :name", Role.class)
                    .setParameter("name", rolename)
                    .getSingleResult();
            System.out.println("Role retrieved from database: " + role);
            return role;
        } catch (NoResultException ex) {
            System.out.println("No role found with name: " + rolename);
            return null;
        }
    }


    @Transactional
    public Role createRoleIfNotFound(String name, long id) {
        Role role = getRoleByName(name);
        if (role == null) {
            role = new Role(name);
            save(role);
        }
        return role;
    }

}
