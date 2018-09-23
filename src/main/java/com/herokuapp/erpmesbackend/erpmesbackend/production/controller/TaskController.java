package com.herokuapp.erpmesbackend.erpmesbackend.production.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.TaskRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.TaskRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        List<TaskDTO> taskDTOs = new ArrayList<>();
        tasks.forEach(task -> taskDTOs.add(new TaskDTO(task)));
        return taskDTOs;
    }

    @GetMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getOneTask(@PathVariable("id") Long id) {
        checkIfTaskExists(id);
        return new TaskDTO(taskRepository.findById(id).get());
    }

//    @GetMapping(value = "/employees/{id}/tasks")
//    @ResponseStatus(HttpStatus.OK)
//    public List<TaskDTO> getTasksByAssignee(@PathVariable("id") Long id) {
//        checkIfAssigneeExists(id);
//        if (!taskRepository.findByAssigneeId(id).isPresent()) {
//            return new ArrayList<>();
//        }
//        List<Task> tasks = taskRepository.findByAssigneeId(id).get();
//        List<TaskDTO> taskDTOs = new ArrayList<>();
//        tasks.forEach(task -> taskDTOs.add(new TaskDTO(task)));
//        return taskDTOs;
//    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO addOneTask(@RequestBody TaskRequest taskRequest) {
        String name = taskRequest.getName();

        Employee assignee = null;
        if (taskRequest.getAssigneeId() != null) {
            checkIfAssigneeExists(taskRequest.getAssigneeId());
            assignee = employeeRepository.findById(taskRequest.getAssigneeId()).get();
        }

        List<Long> precedingTaskIds = new ArrayList<>();
        if (taskRequest.getPrecedingTaskIds() != null && !taskRequest.getPrecedingTaskIds().isEmpty()) {
            taskRequest.getPrecedingTaskIds().forEach(this::checkIfTaskExists);
            precedingTaskIds.addAll(taskRequest.getPrecedingTaskIds());
        }

        Integer estimatedTime = taskRequest.getEstimatedTime();
        LocalDateTime deadline = taskRequest.getDeadline();

        LocalDateTime scheduledTime = null;
        if (taskRequest.getScheduledTime() != null) {
            scheduledTime = taskRequest.getScheduledTime();
        }

        LocalDateTime startTime = null;
        if (taskRequest.getStartTime() != null) {
            startTime = taskRequest.getStartTime();
        }

        LocalDateTime endTime = null;
        if (taskRequest.getEndTime() != null) {
            endTime = taskRequest.getEndTime();
        }

        String details = null;
        if (taskRequest.getDetails() != null) {
            details = taskRequest.getDetails();
        }

        Type type = null;
        if (taskRequest.getType() != null) {
            type = taskRequest.getType();
        }

        Long reference = null;
        if (taskRequest.getReference() != null) {
            reference = taskRequest.getReference();
        }

        Task task = new Task(name, precedingTaskIds, assignee, scheduledTime, estimatedTime, deadline, details, type,
                reference);

        taskRepository.save(task);
        return new TaskDTO(task);
    }

    @PutMapping("/tasks/{id}")
    public HttpStatus updateTask(@PathVariable("id") Long id, @RequestBody TaskRequest taskRequest) {
        checkIfTaskExists(id);
        Task task = taskRepository.findById(id).get();

        task.setName(taskRequest.getName());

        Employee assignee = null;
        if (taskRequest.getAssigneeId() != null) {
            checkIfAssigneeExists(taskRequest.getAssigneeId());
            assignee = employeeRepository.findById(taskRequest.getAssigneeId()).get();
        }

        LocalDateTime deadline = taskRequest.getDeadline();

        LocalDateTime scheduledTime = null;
        if (taskRequest.getScheduledTime() != null) {
            scheduledTime = taskRequest.getScheduledTime();
        }

        LocalDateTime startTime = null;
        if (taskRequest.getStartTime() != null) {
            startTime = taskRequest.getStartTime();
        }

        LocalDateTime endTime = null;
        if (taskRequest.getEndTime() != null) {
            endTime = taskRequest.getEndTime();
        }

        String details = null;
        if (taskRequest.getDetails() != null) {
            details = taskRequest.getDetails();
        }
        taskRepository.save(task);
        return HttpStatus.NO_CONTENT;
    }

    @DeleteMapping("/tasks/{id}")
    public HttpStatus removeTask(@PathVariable("id") Long id) {
        checkIfTaskExists(id);
        List<Task> collected = taskRepository.findAll().stream()
                .filter(task -> task.getPrecedingTaskIds().contains(id))
                .collect(Collectors.toList());
        collected.forEach(task -> task.getPrecedingTaskIds().remove(id));
        taskRepository.delete(taskRepository.findById(id).get());
        return HttpStatus.OK;
    }

    private void checkIfTaskExists(Long id) {
        if (!taskRepository.findById(id).isPresent())
            throw new NotFoundException("Such task doesn't exist!");
    }

    private void checkIfAssigneeExists(Long id) {
        if (!employeeRepository.findById(id).isPresent())
            throw new NotFoundException("Chosen assignee doesn't exist!");
    }

    private void checkIfCategoryOfTaskMayBeDoing(Category category) {
        if (!category.equals(Category.TODO))
            throw new InvalidRequestException("The task can't have 'DOING' category!");
    }

    private void checkIfCategoryOfTaskMayBeDone(Category category) {
        if (!category.equals(Category.DOING))
            throw new InvalidRequestException("The task can't have 'DONE' category!");
    }
}
