package com.herokuapp.erpmesbackend.erpmesbackend.shop.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}
