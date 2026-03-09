package com.example.mamatolmi.domain.aiResponse.repository;

import com.example.mamatolmi.domain.aiResponse.entity.AiResponse;
import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiResponseRepository extends JpaRepository<AiResponse, Long> {
    Optional<AiResponse> findByKidsNote(KidsNote kidsNote);
}
