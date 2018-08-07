package com.herokuapp.erpmesbackend.erpmesbackend.holidays;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotAManagerException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
        if (request.getHolidayType().equals(HolidayType.VACATION)) {
            checkIfCanTakeDaysOff(id, request);
        }
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
        if (employees.size() == 0) {
            return new ArrayList<>();
        }
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

    private void checkIfCanTakeDaysOff(long id, HolidayRequest request) {
        Employee employee = employeeRepository.findById(id).get();
        if (holidayRepository.findByEmployeeId(id).isPresent()) {
            List<Holiday> holidays = holidayRepository.findByEmployeeId(id).get();
            int daysOffTaken = holidays.stream()
                    .filter(holiday -> holiday.getHolidayType().equals(HolidayType.VACATION))
                    .filter(holiday -> holiday.getStartDate().getYear() == LocalDate.now().getYear())
                    .map(Holiday::getDuration)
                    .mapToInt(Integer::intValue)
                    .sum();
            int daysOffPerYear = employee.getContract().getDaysOffPerYear();
            int requestedDuration = 0;
            for(int i = 0; i < request.getDuration(); i++) {
                if(!request.getStartDate().plusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY) &&
                        !request.getStartDate().plusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                    requestedDuration++;
                }
            }
            if (daysOffTaken == daysOffPerYear) {
                throw new InvalidRequestException("You've already used all your vacation days for this year!");
            } else if ((daysOffPerYear - daysOffTaken) < requestedDuration) {
                throw new InvalidRequestException("You don't have enough available days to take such a long vacation this year!");
            }
        }
    }
}
