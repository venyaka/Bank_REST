package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {

    List<CardBlockRequest> findByUser(User user);

    List<CardBlockRequest> findByStatus(CardBlockRequest.Status status);
}

