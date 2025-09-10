package com.example.bankcards.controller;

import com.example.bankcards.dto.response.CardBlockRequestRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.RefreshTokenFilter;
import com.example.bankcards.security.jwt.JwtTokenFilter;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardBlockRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardBlockRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CardBlockRequestService blockRequestService() {
            return org.mockito.Mockito.mock(CardBlockRequestService.class);
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
    private CardBlockRequestService blockRequestService;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private RefreshTokenFilter refreshTokenFilter;


    @Test
    void requestBlockCard_success() throws Exception {
        CardBlockRequestRespDTO resp = new CardBlockRequestRespDTO();
        resp.setId(1L);
        Mockito.when(blockRequestService.createBlockRequest(Mockito.eq(1L), Mockito.any())).thenReturn(resp);

        com.example.bankcards.dto.response.UserRespDTO userDto = new com.example.bankcards.dto.response.UserRespDTO();
        userDto.setEmail("user@gmail.com");
        userDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(userDto);
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        Mockito.when(userRepository.findByEmail("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(post("/cards/1/block-request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getMyBlockRequests_success() throws Exception {
        CardBlockRequestRespDTO resp = new CardBlockRequestRespDTO();
        resp.setId(1L);
        Mockito.when(blockRequestService.getUserBlockRequests(Mockito.any())).thenReturn(Collections.singletonList(resp));

        com.example.bankcards.dto.response.UserRespDTO userDto = new com.example.bankcards.dto.response.UserRespDTO();
        userDto.setEmail("user@gmail.com");
        userDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(userDto);
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        Mockito.when(userRepository.findByEmail("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(get("/cards/block-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
