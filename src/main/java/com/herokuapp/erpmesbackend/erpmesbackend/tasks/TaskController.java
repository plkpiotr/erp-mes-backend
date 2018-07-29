package com.herokuapp.erpmesbackend.erpmesbackend.tasks;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Category category = taskRequest.getCategory();
        Employee assignee = employeeRepository.findById(taskRequest.getAssigneeId()).get();

        boolean isReadyToDoing = true;
        List<Task> precedingTasks = new ArrayList<>();

        if (!taskRequest.getPrecedingTasksId().isEmpty()) {
            taskRequest.getPrecedingTasksId().forEach(id -> precedingTasks.add(taskRepository.findById(id).get()));
            if (!checkReadyToDoing(precedingTasks))
                isReadyToDoing = false;
        }

        int estimatedTimeInMinutes = taskRequest.getEstimatedTimeInMinutes();
        LocalDateTime deadline = taskRequest.getDeadline();

        Task task = new Task(category, assignee, precedingTasks, isReadyToDoing, estimatedTimeInMinutes, deadline);
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

    // TODO: Check it:
    private boolean checkReadyToDoing(List<Task> precedingTasks) {
        for (Task task : precedingTasks) {
            if (precedingTasks.stream().noneMatch(t -> t.getCategory().name().contains("DONE")))
                return false;
        }
        return true;
    }

    private TaskDTO mapToDTO(Task task) {

        String name = task.getName();
        String category = task.getCategory().name();
        String assignee = task.getAssignee().getFirstName() + task.getAssignee().getLastName();

        List<String> precedingTasks = new ArrayList<>();
        task.getPrecedingTasks().stream().map(Task::getName).forEach(precedingTasks::add);

        String details = task.getDetails();
        boolean isReadyToDoing = task.isReadyToDoing();
        int estimatedTimeInMinutes = task.getEstimatedTimeInMinutes();
        String deadline = String.valueOf(task.getDeadline());
        String creationTime = String.valueOf(task.getCreationTime());

        String startTime = Optional.ofNullable(String.valueOf(task.getStartTime())).orElse("Not yet started");
        String endTime = Optional.ofNullable(String.valueOf(task.getEndTime())).orElse("Not done");

        return new TaskDTO(name, category, assignee, precedingTasks, details, isReadyToDoing, estimatedTimeInMinutes,
                deadline, creationTime, startTime, endTime);
    }
}
