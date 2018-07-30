package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRequest;
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
    protected TeamRequest teamRequest;
    protected HolidayRequest holidayRequest;

    protected List<EmployeeRequest> employeeRequests;
    protected List<EmployeeRequest> adminRequests;
    protected List<EmployeeRequest> nonAdminRequests;
    protected List<TeamRequest> teamRequests;
    protected List<HolidayRequest> holidayRequests;

    protected List<Long> availableManagerIds;
    protected List<Long> availableEmployeeIds;

    protected EmployeeFactory employeeFactory;
    protected HolidayFactory holidayFactory;

    public FillBaseTemplate() {
        employeeFactory = new EmployeeFactory();
        holidayFactory = new HolidayFactory();

        employeeRequests = new ArrayList<>();
        adminRequests = new ArrayList<>();
        nonAdminRequests = new ArrayList<>();
        teamRequests = new ArrayList<>();
        holidayRequests = new ArrayList<>();
    }

    protected void addOneEmployeeRequest(boolean shouldPost) {
        employeeRequest = employeeFactory.generateEmployeeRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", employeeRequest, Employee.class);
        }
    }

    protected void addOneAdminRequest(boolean shouldPost) {
        adminRequest = employeeFactory.generateAdminRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", adminRequest, Employee.class);
        }
    }

    protected void addOneNonAdminRequest(boolean shouldPost) {
        nonAdminRequest = employeeFactory.generateNonAdminRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", nonAdminRequest, Employee.class);
        }
    }

    protected void addEmployeeRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            employeeRequests.add(employeeFactory.generateEmployeeRequest());
        }
        if (shouldPost) {
            employeeRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    request, Employee.class));
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

    protected Team addOneTeamRequest(boolean shouldPost, TeamRequest request) {
        checkEmployeeBase();
        Long[] employeeValues = {availableEmployeeIds.get(0), availableEmployeeIds
                .get(availableEmployeeIds.size() - 1)};

        Role role = employeeFactory.generateRole();
        Employee manager = restTemplate.getForEntity("/employees/{id}", Employee.class,
                availableManagerIds.get(0)).getBody();
        Employee[] employees = {
                restTemplate.getForEntity("/employees/{id}", Employee.class, availableEmployeeIds
                        .get(0)).getBody(),
                restTemplate.getForEntity("/employees/{id}", Employee.class, availableEmployeeIds
                        .get(availableEmployeeIds.size()-1)).getBody()
        };

        request = new TeamRequest(role,
                availableManagerIds.get(0), Arrays.asList(employeeValues));
        removeUsedIds();
        if (shouldPost) {
            restTemplate.postForEntity("/teams", request, Team.class);
        }
        return new Team(role, manager, Arrays.asList(employees));
    }

    protected List<Team> addTeamRequests(boolean shouldPost) {
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            teamRequests.add(new TeamRequest());
            teams.add(addOneTeamRequest(shouldPost, teamRequests.get(i)));
        }
        return teams;
    }

    private void removeUsedIds() {
        availableManagerIds.remove(0);
        availableEmployeeIds.remove(0);
        availableEmployeeIds.remove(availableEmployeeIds.size() - 1);
    }

    private void checkEmployeeBase() {
        if (availableManagerIds == null &&
                availableEmployeeIds == null) {
            filterEmployeesByRole();
        }
        boolean shouldRefilter = false;
        if (availableManagerIds.size() == 0) {
            addOneAdminRequest(true);
            shouldRefilter = true;
        }
        if (availableEmployeeIds.size() < 2) {
            addNonAdminRequests(true);
            shouldRefilter = true;
        }
        if (shouldRefilter) {
            filterEmployeesByRole();
        }
    }

    public void filterEmployeesByRole() {
        List<Employee> employees = Arrays.asList(restTemplate
                .getForEntity("/employees", Employee[].class).getBody());

        availableManagerIds = employees.stream()
                .filter(employee -> employee.isManager())
                .map(employee -> employee.getId())
                .collect(Collectors.toList());

        availableEmployeeIds = employees.stream()
                .filter(employee -> !employee.isManager())
                .map(employee -> employee.getId())
                .collect(Collectors.toList());

        List<Team> teams = Arrays.asList(restTemplate
                .getForEntity("/teams", Team[].class).getBody());

        List<Long> collectManagerIds = teams.stream()
                .map(team -> team.getManager().getId())
                .collect(Collectors.toList());

        List<Long> collectEmployeeIds = new ArrayList<>();
        teams.forEach(team -> team.getEmployees().forEach(
                employee -> collectEmployeeIds.add(employee.getId())
        ));

        availableManagerIds.removeAll(collectManagerIds);
        availableEmployeeIds.removeAll(collectEmployeeIds);
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
