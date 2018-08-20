package com.herokuapp.erpmesbackend.erpmesbackend.tasks;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
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
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskRepository.findAll());
    }

    @GetMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Task getOneTask(@PathVariable("id") Long id) {
        checkIfTaskExists(id);
        return taskRepository.findById(id).get();
    }

    @GetMapping(value = "/employees/{id}/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<Task> getTasksByAssignee(@PathVariable("id") Long id) {
        checkIfAssigneeExists(id);
        return taskRepository.findByAssigneeId(id).isPresent() ?
                taskRepository.findByAssigneeId(id).get() : new ArrayList<>();
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Task addOneTask(@RequestBody TaskRequest taskRequest) {
        String name = taskRequest.getName();
        Category category = taskRequest.getCategory();

        Employee assignee = null;
        if (taskRequest.getAssigneeId() != null) {
            checkIfAssigneeExists(taskRequest.getAssigneeId());
            assignee = employeeRepository.findById(taskRequest.getAssigneeId()).get();
        }

        List<Task> precedingTasks = new ArrayList<>();
        if (taskRequest.getPrecedingTaskIds() != null) {
            taskRequest.getPrecedingTaskIds().forEach(this::checkIfTaskExists);
            taskRequest.getPrecedingTaskIds().forEach(id -> precedingTasks.add(taskRepository.findById(id).get()));
        }

        String details = taskRequest.getDetails();

        Integer estimatedTimeInMinutes = null;
        if (taskRequest.getEstimatedTimeInMinutes() != null)
            estimatedTimeInMinutes = taskRequest.getEstimatedTimeInMinutes();

        LocalDateTime deadline = taskRequest.getDeadline();

        Type type = null;
        if (taskRequest.getType() != null)
            type = taskRequest.getType();

        Long reference = null;
        if (taskRequest.getReference() != null)
            reference = taskRequest.getReference();

        LocalDateTime scheduledTime = null;
        if (taskRequest.getScheduledTime() != null)
            scheduledTime = taskRequest.getScheduledTime();

        Task task = new Task(name, category, assignee, precedingTasks, details, estimatedTimeInMinutes, deadline, type,
                reference, scheduledTime);
        taskRepository.save(task);
        return task;
    }

    @PutMapping("/tasks/{id}")
    public HttpStatus updateTask(@PathVariable("id") Long id, @RequestBody TaskRequest taskRequest) {
        checkIfTaskExists(id);

        Task task = taskRepository.findById(id).get();

        task.setName(taskRequest.getName());
        task.setAssignee(employeeRepository.findById(taskRequest.getAssigneeId()).get());

        List<Task> precedingTasks = new ArrayList<>();
        if (taskRequest.getPrecedingTaskIds() != null)
            taskRequest.getPrecedingTaskIds().forEach(index -> precedingTasks.add(taskRepository.findById(index).get()));
        task.setPrecedingTasks(precedingTasks);

        task.setDetails(taskRequest.getDetails());

        if (taskRequest.getEstimatedTimeInMinutes() != null)
            taskRequest.setEstimatedTimeInMinutes(taskRequest.getEstimatedTimeInMinutes());

        task.setDeadline(taskRequest.getDeadline());

        if (taskRequest.getType() != null)
            taskRequest.setType(taskRequest.getType());

        if (taskRequest.getReference() != null)
            taskRequest.setReference(taskRequest.getReference());

        if (taskRequest.getScheduledTime() != null)
            taskRequest.setScheduledTime(taskRequest.getScheduledTime());

        taskRepository.save(task);
        return HttpStatus.NO_CONTENT;
    }

    @PatchMapping("/tasks/{id}")
    public HttpStatus changeTaskCategory(@PathVariable("id") Long id) {
        checkIfTaskExists(id);
        Task task = taskRepository.findById(id).get();

        if (task.getCategory().equals(Category.DOING)) {
            checkIfCategoryOfTaskMayBeDoing(task.getCategory());
            task.setCategory(Category.DOING);
            task.setStartTime(LocalDateTime.now());
        } else if (task.getCategory().equals(Category.DONE)) {
            checkIfCategoryOfTaskMayBeDone(task.getCategory());
            task.setCategory(Category.DONE);
            task.setEndTime(LocalDateTime.now());
        }

        taskRepository.save(task);
        return HttpStatus.NO_CONTENT;
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

    private void checkIfCategoryOfTaskMayBeDoing(Category category) {
        if (!category.equals(Category.TODO))
            throw new InvalidRequestException("The task can't have 'DOING' category!");
    }

    private void checkIfCategoryOfTaskMayBeDone(Category category) {
        if (!category.equals(Category.DOING))
            throw new InvalidRequestException("The task can't have 'DONE' category!");
    }
}
