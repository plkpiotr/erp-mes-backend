package com.herokuapp.erpmesbackend.erpmesbackend.shop;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Item addNewItem(@RequestBody ItemRequest request) {
        Item item = request.extractItem();
        itemRepository.save(item);
        return item;
    }

    @GetMapping("/items/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Item getOneItem(@PathVariable("id") long id) {
        checkIfItemExists(id);
        return itemRepository.findById(id).get();
    }

    @PostMapping("/items/{id}/supply")
    @ResponseStatus(HttpStatus.OK)
    public Item supplyItem(@PathVariable("id") long id, @RequestBody int q) {
        checkIfItemExists(id);
        Item item = itemRepository.findById(id).get();
        item.supply(q);
        itemRepository.save(item);
        return item;
    }

    @PostMapping("/items/{id}/buy")
    @ResponseStatus(HttpStatus.OK)
    public Item buyItem(@PathVariable("id") long id, @RequestBody int q) {
        checkIfItemExists(id);
        Item item = itemRepository.findById(id).get();
        checkIfIsEnough(item, q);
        item.sell(q);
        itemRepository.save(item);
        return item;
    }

    @PostMapping("/set-special-offer")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> setSpecialOffer(@RequestParam("percentOff") int percentOff,
                                      @RequestParam(required = false, value = "query") String query) {
        return updateItems(percentOff / 100, query);
    }

    @PostMapping("/cancel-special-offer")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> cancelSpecialOffer(@RequestParam(required = false, value = "query") String query) {
        return updateItems(1, query);
    }

    List<Item> updateItems(double multiplier, String query) {
        List<Item> items = itemRepository.findAll();
        if (query != null && !query.equals("")) {
            items = items.stream().filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        List<Item> updatedItems = new ArrayList<>();
        items.forEach(item -> {
            item.setCurrentPrice(item.getOriginalPrice() * multiplier);
            itemRepository.save(item);
            updatedItems.add(item);
        });
        return updatedItems;
    }

    private void checkIfItemExists(long id) {
        if (!itemRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such item doesn't exist!");
        }
    }

    private void checkIfIsEnough(Item item, int q) {
        if (q > item.getQuantity()) {
            throw new InvalidRequestException("There's not enough of this item!");
        }
    }
}
