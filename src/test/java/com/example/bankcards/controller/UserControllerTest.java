package com.example.bankcards.controller;

import com.example.bankcards.dto.request.UpdateCurrentUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.security.RefreshTokenFilter;
import com.example.bankcards.security.jwt.JwtTokenFilter;
import com.example.bankcards.service.UserService;
import com.example.bankcards.service.impl.CookieServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return org.mockito.Mockito.mock(UserService.class);
        }
        @Bean
        public CookieServiceImpl cookieService() {
            return org.mockito.Mockito.mock(CookieServiceImpl.class);
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
    private CookieServiceImpl cookieService;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private RefreshTokenFilter refreshTokenFilter;


    @Test
    void getUserInfo_success() throws Exception {
        UserRespDTO resp = new UserRespDTO();
        resp.setId(1L);
        resp.setEmail("test@gmail.com");
        Mockito.when(userService.getCurrentUserInfo()).thenReturn(resp);
        mockMvc.perform(get("/users/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    void updateCurrentUser_success() throws Exception {
        UpdateCurrentUserReqDTO req = new UpdateCurrentUserReqDTO();
        req.setFirstName("Venya");
        req.setLastName("Ilkov");
        UserRespDTO resp = new UserRespDTO();
        resp.setId(1L);
        resp.setFirstName("Venya");
        resp.setLastName("Ilkov");
        Mockito.when(userService.updateCurrentUser(Mockito.any())).thenReturn(resp);
        mockMvc.perform(patch("/users/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Venya")
                .param("lastName", "Ilkov"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Venya"));
    }

    @Test
    void logout_success() throws Exception {
        mockMvc.perform(post("/users/logout"))
                .andExpect(status().isOk());
        Mockito.verify(userService).logout();
        Mockito.verify(cookieService).clearAuthCookies(Mockito.any());
    }
}
