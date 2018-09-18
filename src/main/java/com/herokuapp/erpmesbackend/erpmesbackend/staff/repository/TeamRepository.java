package com.herokuapp.erpmesbackend.erpmesbackend.staff.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByManagerId(Long id);
    Team findByRole(Role role);
}
