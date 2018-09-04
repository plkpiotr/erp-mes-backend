package com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.ShopService;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.ShopServiceRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.Return;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.ReturnRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.ReturnStatus;
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

    @PatchMapping("/complaints/{id}")
    public HttpStatus updateStatusComplaint(@PathVariable("id") Long id, @RequestBody ComplaintStatus status) {
        shopService.checkIfComplaintExists(id);
        shopService.updateComplaintStatus(id, status);
        return HttpStatus.NO_CONTENT;
    }

    @PatchMapping("/complaints/{id}/resolution")
    public HttpStatus updateComplaintResolution(@PathVariable("id") Long id, @RequestBody Resolution resolution) {
        shopService.checkIfComplaintExists(id);
        shopService.updateComplaintResolution(id, resolution);
        return HttpStatus.NO_CONTENT;
    }
}
