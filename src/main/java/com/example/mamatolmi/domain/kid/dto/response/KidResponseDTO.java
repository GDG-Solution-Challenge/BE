package com.example.mamatolmi.domain.kid.dto.response;

import java.time.LocalDateTime;

public class KidResponseDTO {
    public record KidCreateResult(
            Long kidId,
            LocalDateTime createdAt
    ) {}
}
