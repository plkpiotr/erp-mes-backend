package com.herokuapp.erpmesbackend.erpmesbackend.production.service;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.TaskRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.AssignmentRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public AssignmentService(TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<TaskDTO> getTasksByAssigneeIsNull() {
        if (!taskRepository.findTaskByAssigneeIsNull().isPresent()) {
            return new ArrayList<>();
        }

        List<Task> tasks = taskRepository.findTaskByAssigneeIsNull().get();
        List<TaskDTO> taskDTOs = new ArrayList<>();
        tasks.forEach(task -> taskDTOs.add(new TaskDTO(task)));
        return taskDTOs;
    }

    public void assignToEmployees(AssignmentRequest assignmentRequest) {
        assignmentRequest.getTaskIds().forEach(this::checkIfTaskExists);
        List<Long> taskIds = new ArrayList<>(assignmentRequest.getTaskIds());

        assignmentRequest.getAssigneeIds().forEach(this::checkIfAssigneeExists);
        List<Long> assigneeIds = new ArrayList<>(assignmentRequest.getAssigneeIds());

        HashMap<Long, LocalDateTime> ongoing = new HashMap<>();
        HashMap<Long, LocalDateTime> schedule = new HashMap<>();
        List<Long> done = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        LocalDateTime startTime = assignmentRequest.getStartTime();
        LocalDateTime currentTime = assignmentRequest.getStartTime();
        LocalDateTime endTime = assignmentRequest.getEndTime();

        assigneeIds.forEach(id -> schedule.put(employeeRepository.findById(id).get().getId(), startTime));
        taskIds.forEach(id -> tasks.add(taskRepository.findById(id).get()));

        long seed = System.nanoTime();
        Collections.shuffle(assigneeIds, new Random(seed));

        List<Task> sortedTasks = tasks.stream().sorted(Task::compare).collect(Collectors.toList());

        while (currentTime.isBefore(endTime)) {
            if (ongoing.containsValue(currentTime)) {
                for (Map.Entry<Long, LocalDateTime> entry: ongoing.entrySet()) {
                    if (entry.getValue().isEqual(currentTime)) {
                        done.add(entry.getKey());
                    }
                }
            }
            for (Map.Entry<Long, LocalDateTime> entry: schedule.entrySet()) {
                if (entry.getValue().isEqual(currentTime) || entry.getValue().isBefore(currentTime)) {
                    for (int i = 0; i < sortedTasks.size(); i++) {
                        if (done.containsAll(sortedTasks.get(i).getPrecedingTaskIds())) {
                            sortedTasks.get(i).setAssignee(employeeRepository.findById(entry.getKey()).get());
                            sortedTasks.get(i).setScheduledTime(currentTime);
                            checkIfTasksCanBeAssigned(currentTime.plusMinutes(sortedTasks.get(i).getEstimatedTime()), endTime);
                            ongoing.put(sortedTasks.get(i).getId(), currentTime.plusMinutes(sortedTasks.get(i).getEstimatedTime()));
                            entry.setValue(currentTime.plusMinutes(sortedTasks.get(i).getEstimatedTime()));
                            sortedTasks.remove(i);
                            break;
                        }
                    }
                }
            }
            currentTime = currentTime.plusMinutes(1);
        }

        tasks.forEach(taskRepository::save);
    }

    private void checkIfTasksCanBeAssigned(LocalDateTime counter, LocalDateTime endTime) {
        if (counter.isAfter(endTime)) {
            throw new InvalidRequestException("Too many tasks in a such short time!");
        }
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
}
