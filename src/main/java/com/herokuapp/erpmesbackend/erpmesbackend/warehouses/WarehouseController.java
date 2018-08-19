package com.herokuapp.erpmesbackend.erpmesbackend.warehouses;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseController(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @GetMapping("/warehouses")
    @ResponseStatus(HttpStatus.OK)
    public List<Warehouse> getAllWarehouses() {
        return new ArrayList<>(warehouseRepository.findAll());
    }

    @GetMapping("/warehouses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Warehouse getOneWarehouse(@PathVariable("id") Long id) {
        checkIfWarehouseExists(id);
        return warehouseRepository.findById(id).get();
    }

    @PostMapping("/warehouses")
    @ResponseStatus(HttpStatus.CREATED)
    public Warehouse addOneWarehouse(@RequestBody WarehouseRequest warehouseRequest) {
        String street = warehouseRequest.getStreet();
        String houseNumber = warehouseRequest.getHouseNumber();
        String city = warehouseRequest.getCity();
        String postalCode = warehouseRequest.getCity();

        Warehouse warehouse = new Warehouse(street, houseNumber, city, postalCode);
        warehouseRepository.save(warehouse);
        return warehouse;
    }

    @PutMapping("/warehouses/{id}")
    public HttpStatus updateWarehouse(@PathVariable("id") Long id, @RequestBody WarehouseRequest warehouseRequest) {
        checkIfWarehouseExists(id);

        Warehouse warehouse = warehouseRepository.findById(id).get();

        warehouse.setStreet(warehouseRequest.getStreet());
        warehouse.setHouseNumber(warehouseRequest.getHouseNumber());
        warehouse.setCity(warehouseRequest.getCity());
        warehouse.setPostalCode(warehouseRequest.getPostalCode());

        warehouseRepository.save(warehouse);
        return HttpStatus.NO_CONTENT;
    }

    @DeleteMapping("/warehouses/{id}")
    public HttpStatus removeWarehouse(@PathVariable("id") Long id) {
        checkIfWarehouseExists(id);
        warehouseRepository.delete(warehouseRepository.findById(id).get());
        return HttpStatus.OK;
    }

    private void checkIfWarehouseExists(Long id) {
        if (!warehouseRepository.findById(id).isPresent())
            throw new NotFoundException("Such warehouse doesn't exist!");
    }
}
