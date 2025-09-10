package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.response.CardRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.RefreshTokenFilter;
import com.example.bankcards.security.jwt.JwtTokenFilter;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CardService cardService() {
            return org.mockito.Mockito.mock(CardService.class);
        }
        @Bean
        public UserService userService() {
            return org.mockito.Mockito.mock(UserService.class);
        }
        @Bean
        public UserRepository userRepository() {
            return org.mockito.Mockito.mock(UserRepository.class);
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
    private UserRepository userRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private RefreshTokenFilter refreshTokenFilter;


    @Test
    void createCard_success() throws Exception {
        CardRespDTO resp = new CardRespDTO();
        resp.setId(1L);
        Mockito.when(cardService.createCard(Mockito.any())).thenReturn(resp);
        mockMvc.perform(post("/admin/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ownerId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void blockCard_success() throws Exception {

        com.example.bankcards.dto.response.UserRespDTO adminDto = new com.example.bankcards.dto.response.UserRespDTO();
        adminDto.setEmail("admin@gmail.com");
        adminDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(adminDto);
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@gmail.com");
        Mockito.when(userRepository.findByEmail("admin@gmail.com")).thenReturn(java.util.Optional.of(admin));
        Mockito.doNothing().when(cardService).blockCard(Mockito.eq(1L), Mockito.any());
        mockMvc.perform(patch("/admin/cards/1/block"))
                .andExpect(status().isOk());
        Mockito.verify(cardService).blockCard(Mockito.eq(1L), Mockito.any());
    }

    @Test
    void activateCard_success() throws Exception {

        com.example.bankcards.dto.response.UserRespDTO adminDto = new com.example.bankcards.dto.response.UserRespDTO();
        adminDto.setEmail("admin@gmail.com");
        adminDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(adminDto);
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@gmail.com");
        Mockito.when(userRepository.findByEmail("admin@gmail.com")).thenReturn(java.util.Optional.of(admin));
        Mockito.doNothing().when(cardService).activateCard(Mockito.eq(1L), Mockito.any());
        mockMvc.perform(patch("/admin/cards/1/activate"))
                .andExpect(status().isOk());
        Mockito.verify(cardService).activateCard(Mockito.eq(1L), Mockito.any());
    }

    @Test
    void deleteCard_success() throws Exception {

        com.example.bankcards.dto.response.UserRespDTO adminDto = new com.example.bankcards.dto.response.UserRespDTO();
        adminDto.setEmail("admin@gmail.com");
        adminDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(adminDto);
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@gmail.com");
        Mockito.when(userRepository.findByEmail("admin@gmail.com")).thenReturn(java.util.Optional.of(admin));
        Mockito.doNothing().when(cardService).deleteCard(Mockito.eq(1L), Mockito.any());
        mockMvc.perform(delete("/admin/cards/1"))
                .andExpect(status().isOk());
        Mockito.verify(cardService).deleteCard(Mockito.eq(1L), Mockito.any());
    }

    @Test
    void getAllCards_success() throws Exception {
        CardRespDTO resp = new CardRespDTO();
        resp.setId(1L);
        Mockito.when(cardService.getAllCards()).thenReturn(Collections.singletonList(resp));
        mockMvc.perform(get("/admin/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void updateCardBalanceForTest_success() throws Exception {

        com.example.bankcards.dto.response.UserRespDTO adminDto = new com.example.bankcards.dto.response.UserRespDTO();
        adminDto.setEmail("admin@gmail.com");
        adminDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(adminDto);
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@gmail.com");
        Mockito.when(userRepository.findByEmail("admin@gmail.com")).thenReturn(java.util.Optional.of(admin));
        Mockito.doNothing().when(cardService).updateCardBalance(Mockito.eq(1L), Mockito.eq(BigDecimal.valueOf(1000)), Mockito.any());
        mockMvc.perform(patch("/admin/cards/1/test-balance?balance=1000"))
                .andExpect(status().isNoContent());
        Mockito.verify(cardService).updateCardBalance(Mockito.eq(1L), Mockito.eq(BigDecimal.valueOf(1000)), Mockito.any());
    }
}
