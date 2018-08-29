package com.herokuapp.erpmesbackend.erpmesbackend.planning;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class PlanningController {

    @Autowired
    private DailyPlanRepository dailyPlanRepository;

    @Autowired
    private SpecialPlanRepository specialPlanRepository;

    @Autowired
    private OrderRepository orderRepository;

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
        DailyPlan dailyPlan = dailyPlanRepository.findById(1L).get();
        dailyPlan.setEmployeesPerDay(request.getEmployeesPerDay());
        dailyPlan.setOrdersPerDay(request.getOrdersPerDay());
        dailyPlan.setReturnsPerDay(request.getReturnsPerDay());
        dailyPlan.setComplaintsResolvedPerDay(request.getComplaintsResolvedPerDay());
        dailyPlanRepository.save(dailyPlan);
        return dailyPlan;
    }

    @GetMapping("/scheduled-orders")
    @ResponseStatus(HttpStatus.OK)
    public int getOrdersScheduledForDay(@RequestParam("when") String when) {
        List<Order> orders = orderRepository.findAll();
        LocalDate today = LocalDate.now();
        if (when.equals("today")) {
            orders = orders.stream()
                    .filter(order -> order.getScheduledFor().equals(today))
                    .collect(Collectors.toList());
        } else if (when.equals("tomorrow")) {
            orders = orders.stream()
                    .filter(order -> order.getScheduledFor().equals(today.plusDays(1)))
                    .collect(Collectors.toList());
        } else if (when.equals("in2days")) {
            orders = orders.stream()
                    .filter(order -> order.getScheduledFor().equals(today.plusDays(2)))
                    .collect(Collectors.toList());
        } else {
            throw new InvalidRequestException("Invalid query parameter!");
        }
        return orders.size();
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
