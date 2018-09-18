package com.herokuapp.erpmesbackend.erpmesbackend.staff.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    Optional<List<Holiday>> findByEmployeeId(long id);

}
