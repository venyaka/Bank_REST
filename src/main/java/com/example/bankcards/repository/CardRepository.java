package com.example.bankcards.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByOwner(User owner);

    @Query("SELECT c FROM Card c WHERE c.owner = :owner AND (:query IS NULL OR LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.status) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Card> searchUserCards(@Param("owner") User owner, @Param("query") String query, Pageable pageable);
}
