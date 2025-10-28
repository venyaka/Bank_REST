package com.example.bankcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserSession;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с сущностями сессий пользователей {@link UserSession}.
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Находит список сессий пользователя по пользователю и времени окончания сессии.
     * @param user пользователь, для которого ищутся сессии.
     * @param localDateTime время окончания сессии.
     * @return список сессий пользователя.
     */
    List<UserSession> findByUserAndEndTime(User user, LocalDateTime localDateTime);

    /**
     * Находит список активных сессий пользователя (у которых время окончания не установлено).
     * @param user пользователь, для которого ищутся сессии.
     * @return список активных сессий пользователя.
     */
    List<UserSession> findByUserAndEndTimeIsNull(User user);

    /**
     * Находит последнюю сессию пользователя, отсортированную по времени начала в порядке убывания.
     * @param user пользователь, для которого ищется сессия.
     * @return последняя сессия пользователя.
     */
    UserSession findFirstByUserOrderByStartTimeDesc(User user);

}
