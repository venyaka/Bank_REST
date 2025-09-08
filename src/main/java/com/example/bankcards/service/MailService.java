package com.example.bankcards.service;

import jakarta.servlet.http.HttpServletRequest;
import com.example.bankcards.entity.User;

public interface MailService {

    void sendUserVerificationMail(User user, HttpServletRequest request);

    void sendPasswordRestoreMail(User user, HttpServletRequest request);
}
