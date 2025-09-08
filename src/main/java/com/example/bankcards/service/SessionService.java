package com.example.bankcards.service;

import com.example.bankcards.entity.UserSession;

public interface SessionService {

    UserSession saveNewSession(Long userId);

}
