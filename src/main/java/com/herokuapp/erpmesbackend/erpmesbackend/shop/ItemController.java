package com.herokuapp.erpmesbackend.erpmesbackend.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

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
        itemService.checkIfItemExists(id);
        return itemRepository.findById(id).get();
    }

    @PostMapping("/items/{id}/supply")
    @ResponseStatus(HttpStatus.OK)
    public Item supplyItem(@PathVariable("id") long id, @RequestBody int q) {
        itemService.checkIfItemExists(id);
        return itemService.supplyItem(id, q);
    }

    @PostMapping("/items/{id}/buy")
    @ResponseStatus(HttpStatus.OK)
    public Item buyItem(@PathVariable("id") long id, @RequestBody int q) {
        itemService.checkIfItemExists(id);
        return itemService.buyItem(id, q);
    }

    @PostMapping("/set-special-offer")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> setSpecialOffer(@RequestParam(value = "percentOff") Double percentOff,
                                      @RequestParam(required = false, value = "query") String query) {
        return itemService.updateItems(1 - percentOff / 100, query);
    }

    @PostMapping("/cancel-special-offer")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> cancelSpecialOffer() {
        return itemService.updateItems(1, "");
    }
}
