package com.herokuapp.erpmesbackend.erpmesbackend.tasks;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:4200")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @GetMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getOneTask(@PathVariable("id") Long id) {
        checkIfTaskExists(id);
        return taskRepository.findById(id).map(this::mapToDTO).get();
    }

    @GetMapping(value = "/tasks", params = {"assigneeId"})
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getTasksByAssignee(@RequestParam("assigneeId") Long assigneeId) {
        checkIfAssigneeExists(assigneeId);
        return taskRepository.findByAssigneeId(assigneeId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Task addNewTask(@RequestBody TaskRequest taskRequest) {
        String name = taskRequest.getName();
        Category category = taskRequest.getCategory();
        Employee assignee = employeeRepository.findById(taskRequest.getAssigneeId()).get();

        boolean isReadyToDoing = true;
        List<Task> precedingTasks = new ArrayList<>();

        if (!taskRequest.getPrecedingTasksIds().isEmpty())
            taskRequest.getPrecedingTasksIds().forEach(id -> precedingTasks.add(taskRepository.findById(id).get()));

        String details = taskRequest.getDetails();
        int estimatedTimeInMinutes = taskRequest.getEstimatedTimeInMinutes();
        LocalDateTime deadline = taskRequest.getDeadline();

        Task task = new Task(name, category, assignee, precedingTasks, details, estimatedTimeInMinutes, deadline);
        taskRepository.save(task);
        return task;
    }

    @DeleteMapping("/tasks/{id}")
    public HttpStatus removeTask(@PathVariable("id") Long id) {
        checkIfTaskExists(id);
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

    private TaskDTO mapToDTO(Task task) {

        String name = task.getName();
        String category = task.getCategory().name();
        String assignee = task.getAssignee().getFirstName() + " " + task.getAssignee().getLastName();

        List<String> precedingTasks = new ArrayList<>();
        task.getPrecedingTasks().stream().map(Task::getName).forEach(precedingTasks::add);

        String details = task.getDetails();
        int estimatedTimeInMinutes = task.getEstimatedTimeInMinutes();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String deadline = task.getDeadline().format(formatter);
        String creationTime = task.getCreationTime().format(formatter);

        String startTime = task.getStartTime() == null ? "----" : task.getStartTime().format(formatter);
        String endTime = task.getEndTime() == null ? "----" : task.getEndTime().format(formatter);

        return new TaskDTO(name, category, assignee, precedingTasks, details, estimatedTimeInMinutes, deadline,
                creationTime, startTime, endTime);
    }
}
