package com.example.mamatolmi.domain.kid.repository;


import com.example.mamatolmi.domain.kid.entity.Kid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KidRepository extends JpaRepository<Kid, Long> {
}
