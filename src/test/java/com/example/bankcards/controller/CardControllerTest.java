package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransferReqDTO;
import com.example.bankcards.dto.response.CardBalanceRespDTO;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

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
    void getCard_success() throws Exception {
        CardRespDTO card = new CardRespDTO();
        card.setId(1L);
        card.setMaskedCardNumber("****1234");
        Mockito.when(cardService.getCardById(Mockito.eq(1L), Mockito.any())).thenReturn(card);

        com.example.bankcards.dto.response.UserRespDTO userDto = new com.example.bankcards.dto.response.UserRespDTO();
        userDto.setEmail("user@gmail.com");
        userDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(userDto);
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        Mockito.when(userRepository.findByEmail("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(get("/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getMyCards_success() throws Exception {
        CardRespDTO card = new CardRespDTO();
        card.setId(1L);
        Mockito.when(cardService.getCardsByOwner(Mockito.any())).thenReturn(Collections.singletonList(card));

        com.example.bankcards.dto.response.UserRespDTO userDto = new com.example.bankcards.dto.response.UserRespDTO();
        userDto.setEmail("user@gmail.com");
        userDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(userDto);
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        Mockito.when(userRepository.findByEmail("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void transfer_success() throws Exception {
        TransferReqDTO req = new TransferReqDTO();
        req.setFromCardId(1L);
        req.setToCardId(2L);
        req.setAmount(BigDecimal.valueOf(100));

        com.example.bankcards.dto.response.UserRespDTO userDto = new com.example.bankcards.dto.response.UserRespDTO();
        userDto.setEmail("user@gmail.com");
        userDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(userDto);
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        Mockito.when(userRepository.findByEmail("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(post("/cards/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fromCardId\":1,\"toCardId\":2,\"amount\":100}"))
                .andExpect(status().isOk());
        Mockito.verify(cardService).transferBetweenCards(Mockito.any(), Mockito.any());
    }

    @Test
    void getCardBalance_success() throws Exception {
        CardBalanceRespDTO resp = new CardBalanceRespDTO();
        resp.setCardId(1L);
        resp.setBalance(BigDecimal.valueOf(500));
        Mockito.when(cardService.getCardBalance(Mockito.eq(1L), Mockito.any())).thenReturn(resp);

        com.example.bankcards.dto.response.UserRespDTO userDto = new com.example.bankcards.dto.response.UserRespDTO();
        userDto.setEmail("user@gmail.com");
        userDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(userDto);
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        Mockito.when(userRepository.findByEmail("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(get("/cards/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500));
    }

    @Test
    void searchUserCards_success() throws Exception {
        CardRespDTO card = new CardRespDTO();
        card.setId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(cardService.searchCards(Mockito.any(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(card)));

        com.example.bankcards.dto.response.UserRespDTO userDto = new com.example.bankcards.dto.response.UserRespDTO();
        userDto.setEmail("user@gmail.com");
        userDto.setId(1L);
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(userDto);
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        Mockito.when(userRepository.findByEmail("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(get("/cards/search?query=test&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }
}
