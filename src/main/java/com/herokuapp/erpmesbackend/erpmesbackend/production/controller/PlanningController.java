package com.herokuapp.erpmesbackend.erpmesbackend.production.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.DailyPlan;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.SpecialPlan;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.DailyPlanRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.SpecialPlanRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.DailyPlanRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.SpecialPlanRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.service.PlanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class PlanningController {

    private final DailyPlanRepository dailyPlanRepository;
    private final SpecialPlanRepository specialPlanRepository;
    private final PlanningService planningService;

    @Autowired
    public PlanningController(DailyPlanRepository dailyPlanRepository, SpecialPlanRepository specialPlanRepository,
                              PlanningService planningService) {
        this.dailyPlanRepository = dailyPlanRepository;
        this.specialPlanRepository = specialPlanRepository;
        this.planningService = planningService;
    }

    @GetMapping("/daily-plan")
    @ResponseStatus(HttpStatus.OK)
    public DailyPlan getDailyPlan() {
        return dailyPlanRepository.findById(1L).get();
    }

    @PutMapping("/daily-plan")
    @ResponseStatus(HttpStatus.OK)
    public DailyPlan updateDailyPlan(@RequestBody DailyPlanRequest request) {
        return planningService.updateDailyPlan(request);
    }

    @GetMapping("/scheduled-orders")
    @ResponseStatus(HttpStatus.OK)
    public int getOrdersScheduledForDay(@RequestParam("when") String when) {
        return planningService.getOrdersForDay(when).size();
    }

    @GetMapping("/scheduled-returns")
    @ResponseStatus(HttpStatus.OK)
    public int getReturnsScheduledForDay(@RequestParam("when") String when) {
        return planningService.getReturnsForDay(when).size();
    }

    @GetMapping("/scheduled-complaints")
    @ResponseStatus(HttpStatus.OK)
    public int getComplaintsScheduledForDay(@RequestParam("when") String when) {
        return planningService.getComplaintsForDay(when).size();
    }

    @GetMapping("/special-plans")
    @ResponseStatus(HttpStatus.OK)
    public List<SpecialPlan> getSpecialPlans() {
        return specialPlanRepository.findAll();
    }

    @GetMapping("/special-plan")
    @ResponseStatus(HttpStatus.OK)
    public SpecialPlan findSpecialPlan(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                       @RequestParam("day") LocalDate day) {
        Optional<List<SpecialPlan>> byDay = specialPlanRepository.findByDay(day);
        return byDay.isPresent() ? byDay.get().get(byDay.get().size()-1) : new SpecialPlan();
    }

    @PostMapping("/special-plan")
    @ResponseStatus(HttpStatus.CREATED)
    public SpecialPlan addSpecialPlan(@RequestBody SpecialPlanRequest request) {
        SpecialPlan specialPlan = request.extractSpecialPlan();
        specialPlanRepository.save(specialPlan);
        return specialPlan;
    }
}
