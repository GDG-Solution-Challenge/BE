package com.example.mamatolmi.domain.activityRecommend.repository;

import com.example.mamatolmi.domain.activityRecommend.entity.ActivityRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRecommendationRepository extends JpaRepository<ActivityRecommendation, Long> {

    List<ActivityRecommendation> findByAge(Integer age);

}
