package com.herokuapp.erpmesbackend.erpmesbackend.shop.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.service.EmailService;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Complaint;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.ComplaintRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.ComplaintStatus;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Resolution;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.service.ShopService;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.ShopServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final ShopService shopService;
    private final EmailService emailService;

    @Autowired
    public ComplaintController(ComplaintRepository complaintRepository, ShopService shopService,
                               EmailService emailService) {
        this.complaintRepository = complaintRepository;
        this.shopService = shopService;
        this.emailService = emailService;
    }

    @GetMapping("/complaints")
    @ResponseStatus(HttpStatus.OK)
    public List<Complaint> getAllOrders() {
        return complaintRepository.findAll();
    }

    @PostMapping("/complaints")
    @ResponseStatus(HttpStatus.CREATED)
    public Complaint addOneComplaint(@RequestBody ShopServiceRequest request) {
        Complaint complaint = shopService.addNewComplaint(request);
        emailService.sensNewComplaintRegisteredMessage(complaint.getId());
        return complaint;
    }

    @GetMapping("/complaints/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Complaint getOneComplaint(@PathVariable("id") Long id) {
        shopService.checkIfComplaintExists(id);
        return complaintRepository.findById(id).get();
    }

    @PutMapping("/complaints/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Complaint updateStatusComplaint(@PathVariable("id") Long id, @RequestBody String status) {
        shopService.checkIfComplaintExists(id);
        emailService.sendComplaintStatusChangeMessage(id, status);
        return shopService.updateComplaintStatus(id, ComplaintStatus.valueOf(status));
    }

    @PutMapping("/complaints/{id}/resolution")
    @ResponseStatus(HttpStatus.OK)
    public Complaint updateComplaintResolution(@PathVariable("id") Long id, @RequestBody String resolution) {
        shopService.checkIfComplaintExists(id);
        emailService.sendComplaintResolutionChangeMessage(id, resolution);
        return shopService.updateComplaintResolution(id, Resolution.valueOf(resolution));
    }
}
