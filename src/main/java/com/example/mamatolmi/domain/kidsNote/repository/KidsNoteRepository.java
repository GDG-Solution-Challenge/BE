package com.example.mamatolmi.domain.kidsNote.repository;

import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KidsNoteRepository extends JpaRepository<KidsNote, Long> {
}
