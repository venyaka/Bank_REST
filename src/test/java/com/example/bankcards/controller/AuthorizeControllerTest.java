package com.example.bankcards.controller;

import com.example.bankcards.dto.request.RegisterReqDTO;
import com.example.bankcards.dto.request.UserAuthorizeReqDTO;
import com.example.bankcards.dto.response.TokenRespDTO;
import com.example.bankcards.security.RefreshTokenFilter;
import com.example.bankcards.security.jwt.JwtTokenFilter;
import com.example.bankcards.service.AuthorizeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorizeController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorizeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AuthorizeService authorizeService() {
            return org.mockito.Mockito.mock(AuthorizeService.class);
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
    private AuthorizeService authorizeService;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private RefreshTokenFilter refreshTokenFilter;


    @Test
    void authorizeUser_success() throws Exception {
        TokenRespDTO resp = new TokenRespDTO();
        resp.setAccessToken("access");
        resp.setRefreshToken("refresh");
        Mockito.when(authorizeService.authorizeUser(Mockito.any(), Mockito.any())).thenReturn(org.springframework.http.ResponseEntity.ok(resp));
        mockMvc.perform(post("/authorize/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@gmail.com\",\"password\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"));
    }

    @Test
    void registerUser_success() throws Exception {
        Mockito.doNothing().when(authorizeService).registerUser(Mockito.any(), Mockito.any());
        mockMvc.perform(post("/authorize/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Venya\",\"lastName\":\"Ilkov\",\"email\":\"test@gmail.com\",\"password\":\"12345678\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void sendVerificationCode_success() throws Exception {
        Mockito.doNothing().when(authorizeService).sendVerificationCode(Mockito.anyString(), Mockito.any());
        mockMvc.perform(post("/authorize/verificateCode?email=test@gmail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void verificateUser_success() throws Exception {
        Mockito.doNothing().when(authorizeService).verificateUser(Mockito.anyString(), Mockito.anyString());
        mockMvc.perform(post("/authorize/verification?email=test@gmail.com&token=abc123"))
                .andExpect(status().isOk());
    }
}
