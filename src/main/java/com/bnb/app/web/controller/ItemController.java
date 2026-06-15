package com.bnb.app.web.controller;

import com.bnb.app.application.dto.CreateItemRequest;
import com.bnb.app.application.dto.ItemResponse;
import com.bnb.app.application.dto.UpdateItemRequest;
import com.bnb.app.application.service.ItemCatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemCatalogService itemCatalogService;

    public ItemController(ItemCatalogService itemCatalogService) {
        this.itemCatalogService = itemCatalogService;
    }

    @GetMapping
    public List<ItemResponse> getAll(@RequestParam(required = false) Boolean inStock) {
        return itemCatalogService.getAll(inStock);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@Valid @RequestBody CreateItemRequest request) {
        return itemCatalogService.create(request);
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody UpdateItemRequest request) {
        return itemCatalogService.update(id, request);
    }
}
