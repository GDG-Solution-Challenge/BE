package com.example.mamatolmi.domain.checklist.repository;

import com.example.mamatolmi.domain.checklist.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    List<Checklist> findByAge(Integer age);
}
