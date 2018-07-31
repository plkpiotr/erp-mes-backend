package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.HolidayFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.HolidayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FillBaseTemplate {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected EmployeeRequest employeeRequest;
    protected EmployeeRequest adminRequest;
    protected EmployeeRequest nonAdminRequest;
    protected HolidayRequest holidayRequest;

    protected List<EmployeeRequest> employeeRequests;
    protected List<EmployeeRequest> adminRequests;
    protected List<EmployeeRequest> nonAdminRequests;
    protected List<HolidayRequest> holidayRequests;

    protected EmployeeFactory employeeFactory;
    protected HolidayFactory holidayFactory;

    public FillBaseTemplate() {
        employeeFactory = new EmployeeFactory();
        holidayFactory = new HolidayFactory();

        employeeRequests = new ArrayList<>();
        adminRequests = new ArrayList<>();
        nonAdminRequests = new ArrayList<>();
        holidayRequests = new ArrayList<>();
    }

    protected void addOneEmployeeRequest(boolean shouldPost) {
        employeeRequest = employeeFactory.generateEmployeeRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", employeeRequest, String.class);
        }
    }

    protected void addOneAdminRequest(boolean shouldPost) {
        adminRequest = employeeFactory.generateAdminRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", adminRequest, String.class);
        }
    }

    protected void addOneNonAdminRequest(boolean shouldPost) {
        nonAdminRequest = employeeFactory.generateNonAdminRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", nonAdminRequest, String.class);
        }
    }

    protected void addEmployeeRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            employeeRequests.add(employeeFactory.generateEmployeeRequest());
        }
        if (shouldPost) {
            employeeRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    request, String.class));
        }
    }

    protected void addAdminRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            adminRequests.add(employeeFactory.generateAdminRequest());
        }
        if (shouldPost) {
            adminRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    request, Employee.class));
        }
    }

    protected void addNonAdminRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            nonAdminRequests.add(employeeFactory.generateNonAdminRequest());
        }
        if (shouldPost) {
            nonAdminRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    request, Employee.class));
        }
    }

    protected void addOneHolidayRequest(long employeeId, boolean shouldPost) {
        holidayRequest = holidayFactory.generateHolidayRequest();
        if(shouldPost) {
            restTemplate.postForEntity("/employees/{id}/holidays", holidayRequest,
                    Holiday.class, employeeId);
        }
    }

    protected void addManyHolidayRequests(long employeeId, boolean shouldPost) {
        for(int i = 0; i < 4; i++) {
            holidayRequests.add(holidayFactory.generateHolidayRequest());
        }
        if(shouldPost) {
            holidayRequests.forEach(request -> restTemplate.postForEntity("/employees/{id}/holidays",
                    request, Holiday.class, employeeId));
        }
    }
}
