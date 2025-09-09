package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardBlockRequestRespDTO;
import com.example.bankcards.entity.User;

import java.util.List;

public interface CardBlockRequestService {

    CardBlockRequestRespDTO createBlockRequest(Long cardId, User user);

    List<CardBlockRequestRespDTO> getUserBlockRequests(User user);

    List<CardBlockRequestRespDTO> getAllBlockRequests();

    CardBlockRequestRespDTO approveBlockRequest(Long requestId, User admin, String comment);

    CardBlockRequestRespDTO rejectBlockRequest(Long requestId, User admin, String comment);
}

