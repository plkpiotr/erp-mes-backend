package com.herokuapp.erpmesbackend.erpmesbackend.production.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {
}
