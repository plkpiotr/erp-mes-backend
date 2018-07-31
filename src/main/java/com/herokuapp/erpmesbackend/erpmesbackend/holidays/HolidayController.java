package com.herokuapp.erpmesbackend.erpmesbackend.holidays;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.EntitiesConflictException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotAManagerException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class HolidayController {

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping("/employees/{id}/holidays")
    @ResponseStatus(HttpStatus.OK)
    public List<Holiday> getAllHolidays(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        return holidayRepository.findByEmployeeId(id).isPresent() ?
                holidayRepository.findByEmployeeId(id).get() : new ArrayList<>();
    }

    @PostMapping("/employees/{id}/holidays")
    @ResponseStatus(HttpStatus.CREATED)
    public Holiday addNewHoliday(@PathVariable("id") long id,
                                 @RequestBody HolidayRequest request) {
        checkIfEmployeeExists(id);
        Holiday holiday = new Holiday(request.getStartDate(), request.getDuration(),
                request.getHolidayType(), employeeRepository.findById(id).get());
        holidayRepository.save(holiday);
        return holiday;
    }

    @GetMapping("/employees/{managerId}/subordinates/holiday-requests")
    @ResponseStatus(HttpStatus.OK)
    public List<Holiday> getHolidays(@PathVariable("managerId") long managerId) {
        checkIfEmployeeExists(managerId);
        checkIfIsManager(managerId);
        List<Holiday> holidays = new ArrayList<>();
        List<Employee> employees = teamRepository.findByManagerId(managerId)
                .get().getEmployees();
        employees.forEach(employee -> holidays.addAll(holidayRepository
                .findByEmployeeId(employee.getId()).get()));
        return holidays.size() > 0 ? holidays : new ArrayList<>();
    }

    @PostMapping("/employees/{managerId}/subordinates/{subordinateId}/holidays")
    @ResponseStatus(HttpStatus.OK)
    public Holiday manageHolidayRequest(@PathVariable("managerId") long managerId,
                                              @PathVariable("subordinateId") long subordinateId,
                                              @RequestBody long holidayId,
                                              @RequestParam(value = "approve") boolean approve) {
        checkEmployees(managerId, subordinateId);
        checkHoliday(holidayId, subordinateId);
        Holiday holiday = holidayRepository.findById(holidayId).get();
        if (approve) {
            holiday.approve();
        } else {
            holiday.decline();
        }
        holidayRepository.save(holiday);
        return holiday;
    }

    private void checkEmployees(long managerId, long subordinateId) {
        checkIfEmployeeExists(managerId);
        checkIfIsManager(managerId);
        checkIfEmployeeExists(subordinateId);
    }

    private void checkHoliday(long holidayId, long subordinateId) {
        checkIfHolidayExists(holidayId);
        checkIfHolidayPending(holidayId);
    }

    private void checkIfHolidayPending(long id) {
        if (!holidayRepository.findById(id).get().getApprovalState().equals(ApprovalState.PENDING)) {
            throw new InvalidRequestException("This holiday has already been managed!");
        }
    }

    private void checkIfHolidayExists(long id) {
        if (!holidayRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such holiday request doesn't exist!");
        }
    }

    private void checkIfEmployeeExists(long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such employee doesn't exist!");
        }
    }

    private void checkIfIsManager(long id) {
        if (!employeeRepository.findById(id).get().isManager()) {
            throw new NotAManagerException("This employee is not a manager and therefore can't have subordinates!");
        }
    }
}
