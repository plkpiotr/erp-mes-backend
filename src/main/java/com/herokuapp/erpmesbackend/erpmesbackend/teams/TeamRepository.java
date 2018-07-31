package com.herokuapp.erpmesbackend.erpmesbackend.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByManagerId(Long id);
    Team findByRole(Role role);
}
