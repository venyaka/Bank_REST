package com.example.bankcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserSession;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findByUserAndEndTime(User user, LocalDateTime localDateTime);

    UserSession findFirstByUserOrderByStartTimeDesc(User user);

}
