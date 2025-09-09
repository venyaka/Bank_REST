package com.example.bankcards.controller.admin;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.request.CreateUserReqDTO;
import com.example.bankcards.dto.request.UpdateUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.USER_ADMIN_CONTROLLER_PATH)
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить всех пользователей (только для администратора)")
    public ResponseEntity<List<UserRespDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @Operation(summary = "Создать пользователя (только для администратора)")
    public ResponseEntity<UserRespDTO> createUser(@RequestBody CreateUserReqDTO createUserReqDTO) {
        return ResponseEntity.ok(userService.createUser(createUserReqDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить пользователя (только для администратора)")
    public ResponseEntity<UserRespDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserReqDTO updateUserReqDTO) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserReqDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя (только для администратора)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
