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

/**
 * Репозиторий для управления сущностями {@link Card}.
 * <p>
 * Предоставляет стандартные методы CRUD через наследование от {@link JpaRepository},
 * а также кастомные методы для поиска карт по владельцу и для постраничного поиска
 * с фильтрацией.
 * </p>
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Находит все карты, принадлежащие указанному пользователю.
     *
     * @param owner Владелец карт ({@link User}).
     * @return Список карт ({@link Card}), принадлежащих пользователю.
     */
    List<Card> findByOwner(User owner);

    /**
     * Выполняет постраничный поиск карт пользователя по заданному запросу.
     * <p>
     * Поиск осуществляется по следующим полям:
     * <ul>
     *   <li>Номер карты ({@code cardNumber}) - частичное совпадение без учета регистра.</li>
     *   <li>Статус карты ({@code status}) - частичное совпадение без учета регистра.</li>
     * </ul>
     * Если поисковый запрос ({@code query}) равен {@code null} или пуст, метод вернет все карты пользователя
     * с учетом пагинации.
     * </p>
     *
     * @param owner    Владелец карт ({@link User}).
     * @param query    Строка для поиска.
     * @param pageable Параметры пагинации (номер страницы, размер, сортировка).
     * @return Страница ({@link Page}) с найденными картами.
     */
    @Query("SELECT c FROM Card c WHERE c.owner = :owner AND (:query IS NULL OR LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.status) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Card> searchUserCards(@Param("owner") User owner, @Param("query") String query, Pageable pageable);
}
