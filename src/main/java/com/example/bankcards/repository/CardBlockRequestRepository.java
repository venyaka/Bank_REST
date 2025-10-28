package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link CardBlockRequest}.
 * <p>
 * Предоставляет стандартные методы CRUD через наследование от {@link JpaRepository},
 * а также кастомные методы для поиска запросов на блокировку по различным критериям.
 * </p>
 */
@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {

    /**
     * Находит все запросы на блокировку, созданные указанным пользователем.
     *
     * @param user Пользователь, чьи запросы необходимо найти.
     * @return Список запросов на блокировку ({@link CardBlockRequest}) для данного пользователя.
     */
    List<CardBlockRequest> findByUser(User user);

    /**
     * Находит все запросы на блокировку с указанным статусом.
     *
     * @param status Статус для поиска (например, PENDING, APPROVED, REJECTED).
     * @return Список запросов на блокировку с заданным статусом.
     */
    List<CardBlockRequest> findByStatus(CardBlockRequest.Status status);
}

