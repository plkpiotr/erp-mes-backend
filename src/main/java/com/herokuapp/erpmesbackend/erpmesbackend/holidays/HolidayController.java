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
//@CrossOrigin("http://localhost:4200")
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
        return employeeRepository.findById(id).get().getHolidays();
    }

    @PostMapping("/employees/{id}/holidays")
    @ResponseStatus(HttpStatus.CREATED)
    public Holiday addNewHoliday(@PathVariable("id") long id,
                                 @RequestBody HolidayRequest request) {
        checkIfEmployeeExists(id);
        Holiday holiday = request.extractHoliday();
        Employee employee = employeeRepository.findById(id).get();
        employee.requestHoliday(holiday);
        holidayRepository.save(holiday);
        employeeRepository.save(employee);
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
        employees.forEach(employee -> holidays.addAll(employee.getHolidays()));
        return holidays;
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
        checkIfManagerAndSubordinateHaveTheSameTeam(managerId, subordinateId);
    }

    private void checkHoliday(long holidayId, long subordinateId) {
        checkIfHolidayExists(holidayId);
        checkIfCorrectHolidayRequestor(holidayId, subordinateId);
        checkIfHolidayPending(holidayId);
    }

    private void checkIfHolidayPending(long id) {
        if (!holidayRepository.findById(id).get().getApprovalState().equals(ApprovalState.PENDING)) {
            throw new InvalidRequestException("This holiday has already been managed!");
        }
    }

    private void checkIfCorrectHolidayRequestor(long holidayId, long subordinateId) {
        List<Holiday> holidays = employeeRepository.findById(subordinateId).get().getHolidays();
        if (holidays.stream().noneMatch(holiday -> holiday.getId() == holidayId)) {
            throw new EntitiesConflictException("This employee didn't make this holiday request!");
        }
    }

    private void checkIfHolidayExists(long id) {
        if (!holidayRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such holiday request doesn't exist!");
        }
    }

    private void checkIfManagerAndSubordinateHaveTheSameTeam(long managerId, long subordinateId) {
        Team team = teamRepository.findByManagerId(managerId).get();
        if (team.getEmployees().stream().noneMatch(employee -> employee.getId() == subordinateId)) {
            throw new EntitiesConflictException("Manager and employee do not belong to the same team!");
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
