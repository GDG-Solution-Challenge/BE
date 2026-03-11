package com.example.mamatolmi.domain.aiResponse.repository;

import com.example.mamatolmi.domain.aiResponse.entity.AiResponse;
import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiResponseRepository extends JpaRepository<AiResponse, Long> {
    Optional<AiResponse> findByKidsNote(KidsNote kidsNote);

    // 특정 자녀의 이번 주 AiResponse 와 KidsNote 를 한 번의 쿼리로 조회
    @Query("SELECT ai FROM AiResponse ai " +
            "JOIN FETCH ai.kidsNote kn " +
            "WHERE kn.kid.id = :kidId " +
            "AND kn.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY kn.createdAt ASC")
    List<AiResponse> findWeeklyByKidId(
            @Param("kidId") Long kidId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
