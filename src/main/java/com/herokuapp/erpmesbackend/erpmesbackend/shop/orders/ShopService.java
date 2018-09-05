package com.herokuapp.erpmesbackend.erpmesbackend.shop.orders;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints.Complaint;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints.ComplaintRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints.ComplaintStatus;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints.Resolution;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryItem;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryService;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.Return;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.ReturnRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShopService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private DeliveryService deliveryService;

    public void checkIfOrderExists(Long id) {
        if (!orderRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such order doesn't exist!");
        }
    }

    public void checkIfReturnExists(Long id) {
        if (!returnRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such return doesn't exist!");
        }
    }

    public void checkIfComplaintExists(Long id) {
        if (!complaintRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such complaint doesn't exist!");
        }
    }

    public Order updateOrderStatus(long id, Status status) {
        Order order = orderRepository.findById(id).get();
        order.setStatus(status);
        orderRepository.save(order);
        return order;
    }

    public Return updateReturnStatus(long id, ReturnStatus status) {
        Return r = returnRepository.findById(id).get();
        r.updateStatus(status);
        returnRepository.save(r);
        return r;
    }

    public Complaint updateComplaintStatus(long id, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(id).get();
        complaint.updateStatus(status);
        complaintRepository.save(complaint);
        return complaint;
    }

    public Complaint updateComplaintResolution(long id, Resolution resolution) {
        Complaint complaint = complaintRepository.findById(id).get();
        complaint.updateResolution(resolution);
        complaintRepository.save(complaint);
        return complaint;
    }

    public Order addNewOrder(ShopServiceRequest request) {
        List<DeliveryItem> deliveryItems = new ArrayList<>();
        request.getDeliveryItemRequests().forEach(deliveryItemRequest -> {
            deliveryService.addDeliveryItem(deliveryItemRequest, deliveryItems);
        });
        Order order = new Order(request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhoneNumber(), request.getStreet(), request.getHouseNumber(),
                request.getCity(), request.getPostalCode(), deliveryItems, request.getScheduledFor());
        orderRepository.save(order);
        return order;
    }

    public Return addNewReturn(ShopServiceRequest request) {
        List<DeliveryItem> deliveryItems = new ArrayList<>();
        request.getDeliveryItemRequests().forEach(deliveryItemRequest -> {
            deliveryService.addDeliveryItem(deliveryItemRequest, deliveryItems);
        });
        Return r = new Return(request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhoneNumber(), request.getStreet(), request.getHouseNumber(),
                request.getCity(), request.getPostalCode(), deliveryItems, request.getScheduledFor());
        returnRepository.save(r);
        return r;
    }

    public Complaint addNewComplaint(ShopServiceRequest request) {
        Resolution requestedResolution = request.getRequestedResolution() == null ?
                Resolution.UNRESOLVED : request.getRequestedResolution();
        List<DeliveryItem> deliveryItems = new ArrayList<>();
        request.getDeliveryItemRequests().forEach(deliveryItemRequest -> {
            deliveryService.addDeliveryItem(deliveryItemRequest, deliveryItems);
        });
        Complaint complaint = new Complaint(requestedResolution, request.getFirstName(), request.getLastName(),
                request.getEmail(), request.getPhoneNumber(), request.getStreet(), request.getHouseNumber(),
                request.getCity(), request.getPostalCode(), deliveryItems, request.getScheduledFor());
        complaintRepository.save(complaint);
        return complaint;
    }

}
