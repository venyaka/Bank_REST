package com.example.bankcards.controller.admin;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.request.CreateUserReqDTO;
import com.example.bankcards.dto.request.UpdateUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для администрирования пользователей.
 * <p>
 * Предоставляет эндпоинты для получения списка всех пользователей, создания, обновления и удаления пользователей.
 * Доступ к методам этого контроллера ограничен и требует наличия у пользователя прав администратора ('ADMIN').
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.USER_ADMIN_CONTROLLER_PATH)
@PreAuthorize("hasAuthority('ADMIN')")
public class UserAdminController {

    private final UserService userService;

    /**
     * Возвращает список всех пользователей системы.
     *
     * @return {@link ResponseEntity} со списком DTO пользователей и статусом 200 OK.
     */
    @GetMapping
    @Operation(summary = "Получить всех пользователей (только для администратора)")
    public ResponseEntity<List<UserRespDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Создает нового пользователя на основе предоставленных данных.
     *
     * @param createUserReqDTO DTO с данными для создания пользователя. Подвергается валидации.
     * @return {@link ResponseEntity} с DTO созданного пользователя и статусом 200 OK.
     */
    @PostMapping
    @Operation(summary = "Создать пользователя (только для администратора)")
    public ResponseEntity<UserRespDTO> createUser(@Valid @RequestBody CreateUserReqDTO createUserReqDTO) {
        return ResponseEntity.ok(userService.createUser(createUserReqDTO));
    }

    /**
     * Обновляет данные существующего пользователя по его идентификатору.
     *
     * @param id               Идентификатор пользователя для обновления.
     * @param updateUserReqDTO DTO с новыми данными для пользователя. Подвергается валидации.
     * @return {@link ResponseEntity} с DTO обновленного пользователя и статусом 200 OK.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Обновить пользователя (только для администратора)")
    public ResponseEntity<UserRespDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserReqDTO updateUserReqDTO) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserReqDTO));
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя для удаления.
     * @return {@link ResponseEntity} со статусом 204 No Content в случае успешного удаления.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя (только для администратора)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
