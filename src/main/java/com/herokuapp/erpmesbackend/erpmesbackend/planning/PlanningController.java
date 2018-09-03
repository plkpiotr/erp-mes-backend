package com.herokuapp.erpmesbackend.erpmesbackend.planning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class PlanningController {

    @Autowired
    private DailyPlanRepository dailyPlanRepository;

    @Autowired
    private SpecialPlanRepository specialPlanRepository;

    @Autowired
    private PlanningService planningService;

    @PostConstruct
    public void init() {
        dailyPlanRepository.save(new DailyPlan());
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

    @GetMapping("/special-plans")
    @ResponseStatus(HttpStatus.OK)
    public List<SpecialPlan> getSpecialPlans() {
        return specialPlanRepository.findAll();
    }

    @GetMapping("/special-plan")
    @ResponseStatus(HttpStatus.OK)
    public SpecialPlan findSpecialPlan(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                       @RequestParam("day") LocalDate day) {
        Optional<SpecialPlan> byDay = specialPlanRepository.findByDay(day);
        return byDay.isPresent() ? byDay.get() : new SpecialPlan();
    }

    @PostMapping("/special-plan")
    @ResponseStatus(HttpStatus.CREATED)
    public SpecialPlan addSpecialPlan(@RequestBody SpecialPlanRequest request) {
        SpecialPlan specialPlan = request.extractSpecialPlan();
        specialPlanRepository.save(specialPlan);
        return specialPlan;
    }
}
