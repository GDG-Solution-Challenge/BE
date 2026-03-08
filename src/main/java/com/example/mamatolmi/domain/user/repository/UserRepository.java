package com.example.mamatolmi.domain.user.repository;

import com.example.mamatolmi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
