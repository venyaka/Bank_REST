package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.security.RefreshTokenFilter;
import com.example.bankcards.security.jwt.JwtTokenFilter;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return org.mockito.Mockito.mock(UserService.class);
        }
        @Bean
        public JwtTokenFilter jwtTokenFilter() {
            return org.mockito.Mockito.mock(JwtTokenFilter.class);
        }
        @Bean
        public RefreshTokenFilter refreshTokenFilter() {
            return org.mockito.Mockito.mock(RefreshTokenFilter.class);
        }
    }

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private RefreshTokenFilter refreshTokenFilter;


    @Test
    void getAllUsers_success() throws Exception {
        UserRespDTO resp = new UserRespDTO();
        resp.setId(1L);
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(resp));
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void createUser_success() throws Exception {
        UserRespDTO resp = new UserRespDTO();
        resp.setId(2L);
        Mockito.when(userService.createUser(Mockito.any())).thenReturn(resp);
        mockMvc.perform(post("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"new@gmail.com\",\"firstName\":\"Venya\",\"lastName\":\"Ilkov\",\"password\":\"12345678\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void updateUser_success() throws Exception {
        UserRespDTO resp = new UserRespDTO();
        resp.setId(1L);
        resp.setFirstName("Veniamin");
        Mockito.when(userService.updateUser(Mockito.eq(1L), Mockito.any())).thenReturn(resp);
        mockMvc.perform(patch("/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Veniamin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Veniamin"));
    }

    @Test
    void deleteUser_success() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(Mockito.eq(1L));
        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(userService).deleteUser(1L);
    }
}
