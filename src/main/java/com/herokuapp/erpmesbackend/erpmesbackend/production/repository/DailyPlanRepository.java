package com.herokuapp.erpmesbackend.erpmesbackend.production.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.DailyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {
}
