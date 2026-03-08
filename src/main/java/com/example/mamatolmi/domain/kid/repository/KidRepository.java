package com.example.mamatolmi.domain.kid.repository;


import com.example.mamatolmi.domain.kid.entity.Kid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KidRepository extends JpaRepository<Kid, Long> {
    List<Kid> findAllByUserId(Long userId);
}
