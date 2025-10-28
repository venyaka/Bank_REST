package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateUserReqDTO;
import com.example.bankcards.dto.request.UpdateUserReqDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.bankcards.dto.request.UpdateCurrentUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.entity.User;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * <p>
 * Расширяет {@link UserDetailsService} для интеграции со Spring Security.
 * Предоставляет методы для получения, создания, обновления и удаления пользователей,
 * а также для управления текущим аутентифицированным пользователем.
 * </p>
 */
public interface UserService extends UserDetailsService {

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @return DTO с информацией о пользователе.
     */
    UserRespDTO getCurrentUserInfo();

    /**
     * Обновляет информацию о текущем аутентифицированном пользователе.
     *
     * @param updateCurrentUserReqDTO DTO с обновленными данными.
     * @return DTO с обновленной информацией о пользователе.
     */
    UserRespDTO updateCurrentUser(UpdateCurrentUserReqDTO updateCurrentUserReqDTO);

    /**
     * Получает пользователя по его ID.
     *
     * @param id ID пользователя.
     * @return DTO с информацией о пользователе.
     * @throws com.example.bankcards.exception.NotFoundException если пользователь с таким ID не найден.
     */
    UserRespDTO getUserById(Long id);

    /**
     * Получает пользователя по его email.
     *
     * @param email Email пользователя.
     * @return DTO с информацией о пользователе.
     * @throws com.example.bankcards.exception.NotFoundException если пользователь с таким email не найден.
     */
    UserRespDTO getUserByEmail(String email);

    /**
     * Преобразует сущность User в UserRespDTO.
     *
     * @param user Сущность пользователя.
     * @return DTO с информацией о пользователе.
     */
    UserRespDTO getResponseDTO(User user);

    /**
     * Выполняет выход текущего пользователя из системы.
     */
    void logout();

    /**
     * Получает список всех пользователей в системе (только для администраторов).
     *
     * @return Список DTO всех пользователей.
     */
    List<UserRespDTO> getAllUsers();

    /**
     * Создает нового пользователя (только для администраторов).
     *
     * @param createUserReqDTO DTO с данными для создания пользователя.
     * @return DTO с информацией о созданном пользователе.
     * @throws com.example.bankcards.exception.BadRequestException если пользователь с таким email уже существует.
     */
    UserRespDTO createUser(CreateUserReqDTO createUserReqDTO);

    /**
     * Обновляет пользователя по его ID (только для администраторов).
     *
     * @param id               ID пользователя для обновления.
     * @param updateUserReqDTO DTO с обновленными данными.
     * @return DTO с обновленной информацией о пользователе.
     * @throws com.example.bankcards.exception.NotFoundException если пользователь не найден.
     */
    UserRespDTO updateUser(Long id, UpdateUserReqDTO updateUserReqDTO);

    /**
     * Удаляет пользователя по его ID (только для администраторов).
     *
     * @param id ID пользователя для удаления.
     * @throws com.example.bankcards.exception.NotFoundException если пользователь не найден.
     */
    void deleteUser(Long id);
}
