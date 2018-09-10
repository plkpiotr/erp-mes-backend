package com.herokuapp.erpmesbackend.erpmesbackend.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailEntityRepository extends JpaRepository<EmailEntity, Long> {
}
