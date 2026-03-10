package com.example.mamatolmi.domain.chat.repository;

import com.example.mamatolmi.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 유저 ID로 모든 채팅방과 연관된 키즈노트(+자녀)를 함께 조회
    // 전체 내역
    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.kidsNote kn " +
            "JOIN FETCH kn.kid k " + // KidsNote 안에 Kids 엔티티가 연결되어 있다고 가정
            "WHERE cr.user.id = :userId " +
            "ORDER BY cr.createdAt asc ")
    List<ChatRoom> findAllByUserIdWithKidsNoteAndKid(@Param("userId") Long userId);

    // 특정 유저의 채팅방 중 금주 데이터만 조회
    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.kidsNote kn " +
            "JOIN FETCH kn.kid k " +
            "WHERE cr.user.id = :userId AND cr.createdAt >= :startDate " +
            "ORDER BY cr.createdAt ASC ")
    List<ChatRoom> findAllByUserIdAndDateAfter(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate);


}
