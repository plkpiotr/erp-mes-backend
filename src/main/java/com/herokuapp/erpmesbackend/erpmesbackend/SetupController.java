package com.herokuapp.erpmesbackend.erpmesbackend;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.*;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.*;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.ContractRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.TeamRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.AdminRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class SetupController {

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder bcryptEncoder;
    private final TeamRepository teamRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final CurrentReportRepository currentReportRepository;
    private final EstimatedCostsRepository estimatedCostsRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public SetupController(EmployeeRepository employeeRepository, BCryptPasswordEncoder bcryptEncoder,
                           TeamRepository teamRepository, DailyPlanRepository dailyPlanRepository,
                           CurrentReportRepository currentReportRepository,
                           EstimatedCostsRepository estimatedCostsRepository, ContractRepository contractRepository) {
        this.employeeRepository = employeeRepository;
        this.bcryptEncoder = bcryptEncoder;
        this.teamRepository = teamRepository;
        this.dailyPlanRepository = dailyPlanRepository;
        this.currentReportRepository = currentReportRepository;
        this.estimatedCostsRepository = estimatedCostsRepository;
        this.contractRepository = contractRepository;
    }

    @GetMapping("/check-setup")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInitialized() {
        return employeeRepository.findByRole(Role.ADMIN).isPresent();
    }

    @PostMapping("/setup-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO setupAdmin(@RequestBody AdminRequest request) {
        Optional<List<Employee>> findAdmin = employeeRepository.findByRole(Role.ADMIN);
        if (findAdmin.isPresent() && !findAdmin.get().isEmpty()) {
            throw new InvalidRequestException("ADMIN already exists!");
        }
        Employee employee = request.extractUser();
        employee.encodePassword(bcryptEncoder.encode(employee.getPassword()));
        contractRepository.save(employee.getContract());
        employeeRepository.save(employee);
        return new EmployeeDTO(employee);
    }

    @PostMapping("/setup-teams")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpStatus setupTeams() {
        List<Team> teams = teamRepository.findAll();
        if (!teams.isEmpty()) {
            throw new InvalidRequestException("Teams were already setup!");
        }
        Optional<List<Employee>> findAdmin = employeeRepository.findByRole(Role.ADMIN);
        if (!findAdmin.isPresent()) {
            throw new InvalidRequestException("The system needs an ADMIN first!");
        }
        Team admins = new Team(Role.ADMIN);
        admins.addEmployee(findAdmin.get().get(0));
        teamRepository.save(admins);
        teamRepository.save(new Team(Role.ACCOUNTANT));
        teamRepository.save(new Team(Role.ANALYST));
        teamRepository.save(new Team(Role.WAREHOUSE));
        return HttpStatus.CREATED;
    }

    @PostMapping("/setup-daily-plan")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpStatus setupDailyPlan() {
        List<DailyPlan> findDailyPlans = dailyPlanRepository.findAll();
        if (!findDailyPlans.isEmpty()) {
            throw new InvalidRequestException("Daily plan has already been setup!");
        }
        dailyPlanRepository.save(new DailyPlan());
        return HttpStatus.CREATED;
    }

    @PostMapping("/setup-reports")
    @ResponseStatus(HttpStatus.CREATED)
    public CurrentReport setupReports() {
        List<EstimatedCosts> findEstimatedCosts = estimatedCostsRepository.findAll();
        List<CurrentReport> findCurrentReports = currentReportRepository.findAll();
        if (!findEstimatedCosts.isEmpty() || !findCurrentReports.isEmpty()) {
            throw new InvalidRequestException("Reports have already been setup!");
        }
        EstimatedCosts estimatedCosts = new EstimatedCosts();
        estimatedCostsRepository.save(estimatedCosts);
        CurrentReport currentReport = new CurrentReport(estimatedCosts);
        currentReportRepository.save(currentReport);

//        //this is only for testing purposes!!!
//        Month months[] = {Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE};
//        for (Month month : months) {
//            currentReport.setStartDate(LocalDate.of(2018, month, 1));
//            Expense expense = new Expense(ExpenseType.SALARIES, 120000.00);
//            currentReport.addExpense(expense);
//            expenseRepository.save(expense);
//            currentReport.addIncome(50000.00);
//            MonthlyReport monthlyReport = new MonthlyReport(currentReport);
//            monthlyReport.setIncome(new ArrayList<>(currentReport.getIncome()));
//            monthlyReport.setExpenses(new ArrayList<>(currentReport.getExpenses()));
//            monthlyReportRepository.save(monthlyReport);
//            currentReport.clearReport();
//        }
//        //only for saving tests
//        currentReport.setStartDate(LocalDate.of(2018, Month.JULY, 1));
//        currentReportRepository.save(currentReport);

        return currentReport;
    }
}
