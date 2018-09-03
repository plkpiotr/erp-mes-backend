package com.herokuapp.erpmesbackend.erpmesbackend.planning;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanningService {

    @Autowired
    private DailyPlanRepository dailyPlanRepository;

    @Autowired
    private OrderRepository orderRepository;

    public DailyPlan updateDailyPlan(DailyPlanRequest request) {
        DailyPlan dailyPlan = dailyPlanRepository.findById(1L).get();
        dailyPlan.setEmployeesPerDay(request.getEmployeesPerDay());
        dailyPlan.setOrdersPerDay(request.getOrdersPerDay());
        dailyPlan.setReturnsPerDay(request.getReturnsPerDay());
        dailyPlan.setComplaintsResolvedPerDay(request.getComplaintsResolvedPerDay());
        dailyPlanRepository.save(dailyPlan);
        return dailyPlan;
    }

    public List<Order> getOrdersForDay(String when) {
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
        return orders;
    }
}
