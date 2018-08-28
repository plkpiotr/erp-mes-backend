package com.herokuapp.erpmesbackend.erpmesbackend.planning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SpecialPlanRepository extends JpaRepository<SpecialPlan, Long> {

    Optional<SpecialPlan> findByDay(LocalDate day);
}
