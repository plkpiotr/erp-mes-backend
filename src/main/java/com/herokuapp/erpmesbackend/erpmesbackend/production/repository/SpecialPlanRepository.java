package com.herokuapp.erpmesbackend.erpmesbackend.production.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.SpecialPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialPlanRepository extends JpaRepository<SpecialPlan, Long> {

    Optional<List<SpecialPlan>> findByDay(LocalDate day);
}
