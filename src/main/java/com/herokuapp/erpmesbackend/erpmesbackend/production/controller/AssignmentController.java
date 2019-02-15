package com.herokuapp.erpmesbackend.erpmesbackend.production.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.AssignmentRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/assignment")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getTasksByAssigneeIsNull() {
        return assignmentService.getTasksByAssigneeIsNull();
    }

    @PutMapping("/assignment")
    @ResponseStatus(HttpStatus.OK)
    public HttpStatus assignToEmployees(@RequestBody AssignmentRequest assignmentRequest) {
        assignmentService.assignToEmployees(assignmentRequest);
        return HttpStatus.OK;
    }
}
