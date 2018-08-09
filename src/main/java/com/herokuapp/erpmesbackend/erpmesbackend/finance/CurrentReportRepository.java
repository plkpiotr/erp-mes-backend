package com.herokuapp.erpmesbackend.erpmesbackend.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentReportRepository extends JpaRepository<CurrentReport, Long> {
}
