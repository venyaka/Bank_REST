package com.example.bankcards.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ValidationExceptionRespDTO {

    private LocalDateTime timestamp;

    private Long status;

    private String error;

    private String message;

    private String path;

    private String debugInfo;

    private List<ConstraintFailRespDTO> constraintFailRespDTOList;
}
