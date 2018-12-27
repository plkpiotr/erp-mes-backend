package com.herokuapp.erpmesbackend.erpmesbackend.staff.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.service.EmployeeService;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.HolidayRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.HolidayRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.service.HolidayService;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.HolidayType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class HolidayController {

    private final HolidayRepository holidayRepository;
    private final HolidayService holidayService;
    private final EmployeeService employeeService;

    @Autowired
    public HolidayController(HolidayRepository holidayRepository, HolidayService holidayService,
                             EmployeeService employeeService) {
        this.holidayRepository = holidayRepository;
        this.holidayService = holidayService;
        this.employeeService = employeeService;
    }

    @GetMapping("/employees/{id}/holidays")
    @ResponseStatus(HttpStatus.OK)
    public List<Holiday> getAllHolidays(@PathVariable("id") long id) {
        employeeService.checkIfEmployeeExists(id);
        return holidayRepository.findByEmployeeId(id).isPresent() ?
                holidayRepository.findByEmployeeId(id).get() : new ArrayList<>();
    }

    @PostMapping("/employees/{id}/holidays")
    @ResponseStatus(HttpStatus.CREATED)
    public Holiday addNewHoliday(@PathVariable("id") long id,
                                 @RequestBody HolidayRequest request) throws AccessDeniedException {
        employeeService.checkIfEmployeeExists(id);
        employeeService.checkIfUserLoggedIn(id);
        if (request.getHolidayType().equals(HolidayType.VACATION)) {
            holidayService.checkIfCanTakeDaysOff(id, request);
        }
        return holidayService.addHoliday(request, id);
    }

    @GetMapping("/employees/{managerId}/subordinates/holiday-requests")
    @ResponseStatus(HttpStatus.OK)
    public List<Holiday> getHolidays(@PathVariable("managerId") long managerId) {
        employeeService.checkIfEmployeeExists(managerId);
        employeeService.checkIfIsManager(managerId);
        return holidayService.getHolidayRequests(managerId);
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
        employeeService.checkIfEmployeeExists(managerId);
        employeeService.checkIfIsManager(managerId);
        employeeService.checkIfEmployeeExists(subordinateId);
    }

    private void checkHoliday(long holidayId, long subordinateId) {
        holidayService.checkIfHolidayExists(holidayId);
        holidayService.checkIfHolidayPending(holidayId);
    }
}
