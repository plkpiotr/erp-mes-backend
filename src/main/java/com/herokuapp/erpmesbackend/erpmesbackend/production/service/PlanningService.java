package com.herokuapp.erpmesbackend.erpmesbackend.production.service;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.DailyPlan;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.DailyPlanRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.DailyPlanRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Complaint;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Return;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.ComplaintRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.OrderRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.ReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanningService {

    private final DailyPlanRepository dailyPlanRepository;
    private final OrderRepository orderRepository;
    private final ReturnRepository returnRepository;
    private final ComplaintRepository complaintRepository;

    @Autowired
    public PlanningService(DailyPlanRepository dailyPlanRepository, OrderRepository orderRepository,
                           ReturnRepository returnRepository, ComplaintRepository complaintRepository) {
        this.dailyPlanRepository = dailyPlanRepository;
        this.orderRepository = orderRepository;
        this.returnRepository = returnRepository;
        this.complaintRepository = complaintRepository;
    }

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

    public List<Return> getReturnsForDay(String when) {
        List<Return> returns = returnRepository.findAll();
        LocalDate today = LocalDate.now();
        if (when.equals("today")) {
            returns = returns.stream()
                    .filter(order -> order.getScheduledFor().equals(today))
                    .collect(Collectors.toList());
        } else if (when.equals("tomorrow")) {
            returns = returns.stream()
                    .filter(order -> order.getScheduledFor().equals(today.plusDays(1)))
                    .collect(Collectors.toList());
        } else if (when.equals("in2days")) {
            returns = returns.stream()
                    .filter(order -> order.getScheduledFor().equals(today.plusDays(2)))
                    .collect(Collectors.toList());
        } else {
            throw new InvalidRequestException("Invalid query parameter!");
        }
        return returns;
    }

    public List<Complaint> getComplaintsForDay(String when) {
        List<Complaint> complaints = complaintRepository.findAll();
        LocalDate today = LocalDate.now();
        if (when.equals("today")) {
            complaints = complaints.stream()
                    .filter(order -> order.getScheduledFor().equals(today))
                    .collect(Collectors.toList());
        } else if (when.equals("tomorrow")) {
            complaints = complaints.stream()
                    .filter(order -> order.getScheduledFor().equals(today.plusDays(1)))
                    .collect(Collectors.toList());
        } else if (when.equals("in2days")) {
            complaints = complaints.stream()
                    .filter(order -> order.getScheduledFor().equals(today.plusDays(2)))
                    .collect(Collectors.toList());
        } else {
            throw new InvalidRequestException("Invalid query parameter!");
        }
        return complaints;
    }
}
