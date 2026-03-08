package com.example.mamatolmi.domain.kid.dto.request;

import com.example.mamatolmi.domain.kid.enums.Gender;

import java.time.LocalDate;

public class KidRequestDTO {
    public record KidCreate(
            String name,
            Gender gender,
            LocalDate birthDate
    ) {}
}
