package com.example.bankcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.example.bankcards.entity.User;

import java.util.Optional;

/**
 * Репозиторий для управления сущностями {@link User}.
 * <p>
 * Предоставляет стандартные методы CRUD через наследование от {@link JpaRepository},
 * а также возможность выполнения динамических запросов через {@link JpaSpecificationExecutor}.
 * Содержит кастомный метод для поиска пользователя по email.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Находит пользователя по его адресу электронной почты.
     *
     * @param email Email пользователя для поиска.
     * @return {@link Optional}, содержащий найденного пользователя ({@link User}), или пустой, если пользователь не найден.
     */
    Optional<User> findByEmail(String email);
}
