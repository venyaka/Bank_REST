package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.response.CardBlockRequestRespDTO;
import com.example.bankcards.dto.response.UserRespDTO;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCardBlockRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCardBlockRequestControllerTest {

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
    private CardBlockRequestService blockRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private RefreshTokenFilter refreshTokenFilter;


    @Test
    void getAllBlockRequests_success() throws Exception {
        CardBlockRequestRespDTO resp = new CardBlockRequestRespDTO();
        resp.setId(1L);
        Mockito.when(blockRequestService.getAllBlockRequests()).thenReturn(Collections.singletonList(resp));
        mockMvc.perform(get("/admin/cards/block-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void approveBlockRequest_success() throws Exception {
        CardBlockRequestRespDTO resp = new CardBlockRequestRespDTO();
        resp.setId(1L);
        Mockito.when(blockRequestService.approveBlockRequest(Mockito.eq(1L), Mockito.any(), Mockito.eq("ok"))).thenReturn(resp);

        UserRespDTO adminDto = new UserRespDTO();
        adminDto.setEmail("admin@gmail.com");
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(adminDto);
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@gmail.com");
        Mockito.when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));
        mockMvc.perform(post("/admin/cards/block-requests/1/approve?comment=ok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void rejectBlockRequest_success() throws Exception {
        CardBlockRequestRespDTO resp = new CardBlockRequestRespDTO();
        resp.setId(2L);
        Mockito.when(blockRequestService.rejectBlockRequest(Mockito.eq(2L), Mockito.any(), Mockito.eq("bad"))).thenReturn(resp);

        UserRespDTO adminDto = new UserRespDTO();
        adminDto.setEmail("admin@gmail.com");
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(adminDto);
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@gmail.com");
        Mockito.when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));
        mockMvc.perform(post("/admin/cards/block-requests/2/reject?comment=bad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }
}
