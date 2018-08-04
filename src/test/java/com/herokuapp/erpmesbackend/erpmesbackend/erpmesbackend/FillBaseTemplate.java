package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.Task;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.TaskFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.TaskRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.HolidayFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.HolidayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDateTime;
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

    protected TaskRequest taskRequest;
    protected HolidayRequest holidayRequest;

    protected List<EmployeeRequest> employeeRequests;
    protected List<EmployeeRequest> adminRequests;
    protected List<EmployeeRequest> nonAdminRequests;
    protected List<TaskRequest> taskRequests;
    protected List<HolidayRequest> holidayRequests;

    protected EmployeeFactory employeeFactory;
    protected TaskFactory taskFactory;
    protected HolidayFactory holidayFactory;

    public FillBaseTemplate() {
        employeeFactory = new EmployeeFactory();
        taskFactory = new TaskFactory();
        holidayFactory = new HolidayFactory();

        employeeRequests = new ArrayList<>();
        adminRequests = new ArrayList<>();
        nonAdminRequests = new ArrayList<>();
        taskRequests = new ArrayList<>();
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

    public Task addOneTaskRequest(boolean shouldPost, TaskRequest taskRequest) {
        checkEmployeeBase();

        String name = taskFactory.generateName();
        Category category = taskFactory.generateTodoCategory();
        long assigneeId = 1;
        List<Long> precedingTasksIds = new ArrayList<>();
        String details = taskFactory.generateDetails();
        int estimatedTimeInMinutes = taskFactory.generateEstimatedTimeInMinutes();
        LocalDateTime deadline = taskFactory.generateDeadline();

        taskRequest = new TaskRequest(name, category, assigneeId, precedingTasksIds, details,
                estimatedTimeInMinutes, deadline);

        if (shouldPost)
            restTemplate.postForEntity("/tasks", taskRequest, Task.class);

        Employee assignee = restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody();

        List<Task> precedingTasks = new ArrayList<>();
        return new Task(name, category, assignee, precedingTasks, details, estimatedTimeInMinutes, deadline);
    }

    public List<Task> addTaskRequests(boolean shouldPost) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            taskRequests.add(new TaskRequest());
            tasks.add(addOneTaskRequest(shouldPost, taskRequests.get(i)));
        }
        return tasks;
    }

    protected void addManyHolidayRequests(long employeeId, boolean shouldPost) {
        for(int i = 0; i < 4; i++) {
            holidayRequests.add(holidayFactory.generateHolidayRequest());        }
        if(shouldPost) {
            holidayRequests.forEach(request -> restTemplate.postForEntity("/employees/{id}/holidays",
                    request, Holiday.class, employeeId));
        }
    }
}
