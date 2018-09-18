package com.herokuapp.erpmesbackend.erpmesbackend.shop.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
}
