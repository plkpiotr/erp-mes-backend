package com.herokuapp.erpmesbackend.erpmesbackend.production.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Phase;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.NotificationRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.SuggestionRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Indicators;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.TaskRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*")
public class IndicatorsController {

    private final EmployeeRepository employeeRepository;
    private final NotificationRepository notificationRepository;
    private final TaskRepository taskRepository;
    private final SuggestionRepository suggestionRepository;

    public IndicatorsController(EmployeeRepository employeeRepository, NotificationRepository notificationRepository,
                                TaskRepository taskRepository, SuggestionRepository suggestionRepository) {
        this.employeeRepository = employeeRepository;
        this.notificationRepository = notificationRepository;
        this.taskRepository = taskRepository;
        this.suggestionRepository = suggestionRepository;
    }

    @GetMapping("/indicators/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Indicators getIndicators(@PathVariable("id") Long id) {
        Employee assignee = employeeRepository.findById(id).get();

        Indicators i = new Indicators();
        LocalDateTime timeRange = LocalDateTime.now().minusDays(28);

        i.setNumberTasksEmployee(taskRepository.countTasksByAssigneeIdAndCreationTimeAfter(id, timeRange));
        i.setNumberTasksEverybody(taskRepository.countTasksByCreationTimeAfter(timeRange));

        i.setNumberTasksEmployeeToDo(taskRepository.countTasksByAssigneeIdAndCategoryAndCreationTimeAfter(id, Category.TO_DO, timeRange));
        i.setNumberTasksEverybodyToDo(taskRepository.countTasksByCategoryAndCreationTimeAfter(Category.TO_DO, timeRange));
        i.setNumberTasksEmployeeDoing(taskRepository.countTasksByAssigneeIdAndCategoryAndCreationTimeAfter(id, Category.DOING, timeRange));
        i.setNumberTasksEverybodyDoing(taskRepository.countTasksByCategoryAndCreationTimeAfter(Category.DOING, timeRange));
        i.setNumberTasksEmployeeDone(taskRepository.countTasksByAssigneeIdAndCategoryAndCreationTimeAfter(id, Category.DONE, timeRange));
        i.setNumberTasksEverybodyDone(taskRepository.countTasksByCategoryAndCreationTimeAfter(Category.DONE, timeRange));

//        if (taskRepository.countDoneTasksByAssigneeIdAndEndTimeIsLessThanDeadline(id) != null) {
            i.setNumberTasksEmployeeDoneBeforeDeadline(taskRepository.countDoneTasksByAssigneeIdAndEndTimeIsLessThanDeadline(id));
//        }
//        if (taskRepository.countDoneTasksByAssigneeIdAndEndTimeIsLessThanDeadline() != null) {
            i.setNumberTasksEverybodyDoneBeforeDeadline(taskRepository.countDoneTasksByAssigneeIdAndEndTimeIsLessThanDeadline());
//        }

        i.setNumberSuggestionsEmployee(suggestionRepository.countSuggestionsByAuthorIdAndCreationTimeAfter(id, timeRange));
        i.setNumberSuggestionsEverybody(suggestionRepository.countSuggestionsByCreationTimeAfter(timeRange));

        i.setNumberSuggestionsEmployeeReported(suggestionRepository.countSuggestionsByAuthorIdAndPhaseAndCreationTimeAfter(id, Phase.REPORTED, timeRange));
        i.setNumberSuggestionsEverybodyReported(suggestionRepository.countSuggestionsByPhaseAndCreationTimeAfter(Phase.REPORTED, timeRange));
        i.setNumberSuggestionsEmployeeInImplementation(suggestionRepository.countSuggestionsByAuthorIdAndPhaseAndCreationTimeAfter(id, Phase.IN_IMPLEMENTATION, timeRange));
        i.setNumberSuggestionsEverybodyInImplementation(suggestionRepository.countSuggestionsByPhaseAndCreationTimeAfter(Phase.IN_IMPLEMENTATION, timeRange));
        i.setNumberSuggestionsEmployeeImplemented(suggestionRepository.countSuggestionsByAuthorIdAndPhaseAndCreationTimeAfter(id, Phase.IMPLEMENTED, timeRange));
        i.setNumberSuggestionsEverybodyImplemented(suggestionRepository.countSuggestionsByPhaseAndCreationTimeAfter(Phase.IMPLEMENTED, timeRange));

        i.setNumberNotificationsAsTransferee(notificationRepository.countNotificationsByTransfereeIdAndCreationTimeAfter(id, timeRange));
        i.setNumberNotificationsAsConsignee(notificationRepository.countNotificationsByConsigneesIdAndCreationTimeAfter(id, timeRange));

//        if (taskRepository.countAverageDifferenceBetweenDeadlineAndEndTime(id) != null) {
            i.setAverageTimeTasksEmployeeBetweenDeadlineAndEndTime(taskRepository.countAverageDifferenceBetweenDeadlineAndEndTime(id));
//        }
//        if (taskRepository.countAverageDifferenceBetweenDeadlineAndEndTime() != null) {
            i.setAverageTimeTasksEverybodyBetweenDeadlineAndEndTime(taskRepository.countAverageDifferenceBetweenDeadlineAndEndTime());
//        }

//        if (notificationRepository.countAverageDifferenceBetweenStartTimeAndCreationTime(id) != null) {
            i.setAverageTimeNotificationsEmployeeBetweenStartTimeAndCreationTime(notificationRepository.countAverageDifferenceBetweenStartTimeAndCreationTime(id));
//        }
//        if (notificationRepository.countAverageDifferenceBetweenStartTimeAndCreationTime() != null) {
            i.setAverageTimeNotificationsEverybodyBetweenStartTimeAndCreationTime(notificationRepository.countAverageDifferenceBetweenStartTimeAndCreationTime());
//        }

//        if (notificationRepository.countAverageDifferenceBetweenEndTimeAndStartTime(id) != null) {
            i.setAverageTimeNotificationsEmployeeBetweenEndTimeAndStartTime(notificationRepository.countAverageDifferenceBetweenEndTimeAndStartTime(id));
//        }
//        if (notificationRepository.countAverageDifferenceBetweenEndTimeAndStartTime() != null) {
            i.setAverageTimeNotificationsEverybodyBetweenEndTimeAndStartTime(notificationRepository.countAverageDifferenceBetweenEndTimeAndStartTime());
//        }

        return i;
    }

    private void checkIfEmployeeExists(Long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such employee doesn't exist!");
        }
    }
}
