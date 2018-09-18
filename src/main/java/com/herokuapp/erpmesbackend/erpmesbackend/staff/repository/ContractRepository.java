package com.herokuapp.erpmesbackend.erpmesbackend.staff.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
}
