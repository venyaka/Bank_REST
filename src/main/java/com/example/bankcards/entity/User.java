package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Сущность, представляющая пользователя системы.
 * <p>
 * Класс User реализует интерфейс {@link UserDetails} для интеграции со Spring Security.
 * Он содержит основную информацию о пользователе, такую как имя, email, пароль,
 * а также служебные данные: токены, роли и временные метки.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * Уникальный идентификатор пользователя. Генерируется автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     */
    @Column
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Column
    private String lastName;

    /**
     * Email пользователя. Используется в качестве логина для аутентификации.
     */
    @Column
    private String email;

    /**
     * Хешированный пароль пользователя.
     */
    @Column
    private String password;

    /**
     * Флаг, подтверждающий, верифицирован ли email пользователя.
     */
    @Column(name = "is_email_verificated")
    private Boolean isEmailVerificated;

    /**
     * JWT токен (для авторизации пользователя, сброса пароля или подтверждения email).
     */
    @Column(name = "token")
    private String token;

    /**
     * Токен для обновления (refresh) сессии пользователя.
     */
    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * Дата и время создания учетной записи. Устанавливается автоматически.
     */
    @Column(name = "date_create")
    private LocalDateTime dateCreate;

    /**
     * Дата и время последнего обновления данных пользователя. Обновляется автоматически.
     */
    @Column(name = "date_update")
    private LocalDateTime dateUpdate;

    /**
     * Набор ролей пользователя. Определяет уровень доступа к ресурсам системы.
     * Загружается нетерпеливо (EAGER) для нужд Spring Security.
     */
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Set<Role> roles = new HashSet<>();


    /**
     * Метод, выполняемый перед сохранением новой сущности в базу данных.
     * Устанавливает начальные значения для полей {@code isEmailVerificated}, {@code dateCreate}
     * и добавляет роль {@code USER} по умолчанию, если роли не были заданы.
     */
    @PrePersist
    public void prePersist() {
        this.isEmailVerificated = Boolean.FALSE;
        this.dateCreate = LocalDateTime.now();
        if (roles.isEmpty()) {
            roles.add(Role.USER);
        }
    }

    /**
     * Метод, выполняемый перед обновлением существующей сущности.
     * Устанавливает текущее время в поле {@code dateUpdate}.
     */
    @PreUpdate
    public void setDateUpdate() {
        this.dateUpdate = LocalDateTime.now();
    }

    /**
     * Возвращает роли пользователя в виде коллекции {@link GrantedAuthority}.
     * Необходимо для Spring Security.
     * @return Коллекция полномочий.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    /**
     * Возвращает имя пользователя (в данном случае, email).
     * @return Email пользователя.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Указывает, не истек ли срок действия учетной записи.
     * @return {@code true}, если учетная запись действительна.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, не заблокирована ли учетная запись.
     * @return {@code true}, если учетная запись не заблокирована.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, не истек ли срок действия учетных данных (пароля).
     * @return {@code true}, если учетные данные действительны.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, активна ли учетная запись.
     * @return {@code true}, если учетная запись активна.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Сравнивает текущий объект User с другим объектом.
     * Два пользователя считаются равными, если у них одинаковые и не-null идентификаторы (id).
     * @param obj Объект для сравнения.
     * @return {@code true}, если объекты равны, иначе {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id != null && Objects.equals(id, user.id);
    }

    /**
     * Возвращает хеш-код для объекта User.
     * Хеш-код основан на идентификаторе (id), что согласуется с реализацией {@link #equals(Object)}.
     * @return Хеш-код объекта.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
