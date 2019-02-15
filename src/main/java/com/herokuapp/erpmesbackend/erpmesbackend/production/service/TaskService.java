package com.herokuapp.erpmesbackend.erpmesbackend.production.service;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.TaskRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.TaskRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        List<TaskDTO> taskDTOs = new ArrayList<>();
        tasks.forEach(task -> taskDTOs.add(new TaskDTO(task)));
        return taskDTOs;
    }

    public TaskDTO getOneTask(Long id) {
        checkIfTaskExists(id);
        return new TaskDTO(taskRepository.findById(id).get());
    }

    public List<TaskDTO> getTasksByAssignee(Long id) {
        Employee assignee = employeeRepository.findById(id).get();

        checkIfAssigneeExists(assignee.getEmail());
        if (!taskRepository.findTasksByAssigneeIdAndCreationTimeAfterOrderByDeadlineAsc(assignee.getId(),
                LocalDateTime.now().minusDays(28)).isPresent()) {
            return new ArrayList<>();
        }

        List<Task> tasks = taskRepository.findTasksByAssigneeIdAndCreationTimeAfterOrderByDeadlineAsc(assignee.getId(),
                LocalDateTime.now().minusDays(28)).get();
        List<TaskDTO> taskDTOs = new ArrayList<>();
        tasks.forEach(task -> taskDTOs.add(new TaskDTO(task)));
        return taskDTOs;
    }

    public TaskDTO addOneTask(TaskRequest taskRequest) {
        checkIfDeadlineIsBeforeScheduledTime(taskRequest.getDeadline(), taskRequest.getScheduledTime());
        String name = taskRequest.getName();

        Employee author = employeeRepository.findByEmail(getEmailLoggedInUser()).get();

        Employee assignee = null;
        if (taskRequest.getAssigneeId() != null) {
            checkIfAssigneeExists(taskRequest.getAssigneeId());
            assignee = employeeRepository.findById(taskRequest.getAssigneeId()).get();
        }

        List<Long> precedingTaskIds = null;
        if (taskRequest.getPrecedingTaskIds() != null) {
            precedingTaskIds = new ArrayList<>();
            taskRequest.getPrecedingTaskIds().forEach(this::checkIfTaskExists);
            precedingTaskIds.addAll(taskRequest.getPrecedingTaskIds());
        }

        Integer estimatedTime = taskRequest.getEstimatedTime();
        LocalDateTime deadline = taskRequest.getDeadline();

        LocalDateTime scheduledTime = null;
        if (taskRequest.getScheduledTime() != null) {
            scheduledTime = taskRequest.getScheduledTime();
        }

        String details = null;
        if (taskRequest.getDetails() != null) {
            details = taskRequest.getDetails();
        }

        Type type = null;
        if (taskRequest.getType() != null) {
            type = taskRequest.getType();
        }

        Task task = new Task(name, precedingTaskIds, author, assignee, scheduledTime, estimatedTime, deadline, details, type);

        taskRepository.save(task);
        return new TaskDTO(task);
    }

    public TaskDTO setNextCategory(Long id) {
        checkIfTaskExists(id);
        Task task = taskRepository.findById(id).get();

        Employee employee = employeeRepository.findByEmail(getEmailLoggedInUser()).get();

        if (task.getCategory() == Category.TO_DO) {
            task.setCategory(Category.DOING);
            task.setStartTime(LocalDateTime.now());
            task.setStartEmployee(employee);
        } else if (task.getCategory() == Category.DOING) {
            task.setCategory(Category.DONE);
            task.setEndTime(LocalDateTime.now());
            task.setEndEmployee(employee);
        }

        taskRepository.save(task);
        return new TaskDTO(task);
    }

    public void assignToMe(@PathVariable("id") Long id) {
        checkIfTaskExists(id);
        Task task = taskRepository.findById(id).get();
        Employee employee = employeeRepository.findByEmail(getEmailLoggedInUser()).get();

        task.setAssignee(employee);
        taskRepository.save(task);
    }

    private String getEmailLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
    }

    private void checkIfTaskExists(Long id) {
        if (!taskRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such task doesn't exist!");
        }
    }

    private void checkIfAssigneeExists(Long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Chosen assignee doesn't exist!");
        }
    }

    private void checkIfAssigneeExists(String email) {
        if (!employeeRepository.findByEmail(email).isPresent()) {
            throw new NotFoundException("Chosen assignee doesn't exist!");
        }
    }

    private void checkIfDeadlineIsBeforeScheduledTime(LocalDateTime deadline, LocalDateTime scheduledTime) {
        if (deadline != null && scheduledTime != null && deadline.isBefore(scheduledTime)) {
            throw new InvalidRequestException("Deadline can't be earlier than scheduled time!");
        }
    }
}
