package com.example.mamatolmi.domain.kidsNote.repository;

import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface KidsNoteRepository extends JpaRepository<KidsNote, Long> {

    List<KidsNote> findByKidIdAndCreatedAtAfterOrderByCreatedAtAsc(Long kidId, LocalDateTime lastAnalyzedAt);
}
