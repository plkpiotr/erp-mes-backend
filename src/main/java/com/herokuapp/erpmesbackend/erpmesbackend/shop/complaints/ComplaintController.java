package com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.ShopService;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.ShopServiceRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final ShopService shopService;

    public ComplaintController(ComplaintRepository complaintRepository, ShopService shopService) {
        this.complaintRepository = complaintRepository;
        this.shopService = shopService;
    }

    @GetMapping("/complaints")
    @ResponseStatus(HttpStatus.OK)
    public List<Complaint> getAllOrders() {
        return complaintRepository.findAll();
    }

    @PostMapping("/complaints")
    @ResponseStatus(HttpStatus.CREATED)
    public Complaint addOneComplaint(@RequestBody ShopServiceRequest request) {
        return shopService.addNewComplaint(request);
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
        return shopService.updateComplaintStatus(id, ComplaintStatus.valueOf(status));
    }

    @PutMapping("/complaints/{id}/resolution")
    @ResponseStatus(HttpStatus.OK)
    public Complaint updateComplaintResolution(@PathVariable("id") Long id, @RequestBody String resolution) {
        shopService.checkIfComplaintExists(id);
        return shopService.updateComplaintResolution(id, Resolution.valueOf(resolution));
    }
}
