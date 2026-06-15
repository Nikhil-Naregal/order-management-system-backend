package com.bnb.app.application.service;

import com.bnb.app.application.dto.CreateItemRequest;
import com.bnb.app.application.dto.ItemResponse;
import com.bnb.app.application.dto.UpdateItemRequest;
import com.bnb.app.application.mapper.BnbMapper;
import com.bnb.app.domain.model.CategoryEntity;
import com.bnb.app.domain.model.ItemEntity;
import com.bnb.app.domain.repository.CategoryRepository;
import com.bnb.app.domain.repository.ItemRepository;
import com.bnb.app.web.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemCatalogService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final BnbMapper mapper;

    public ItemCatalogService(ItemRepository itemRepository, CategoryRepository categoryRepository, BnbMapper mapper) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAll(Boolean inStock) {
        List<ItemEntity> items = inStock == null
                ? itemRepository.findAllByOrderByNameAsc()
                : itemRepository.findByInStockOrderByNameAsc(inStock);

        return items.stream().map(mapper::toItemResponse).toList();
    }

    public ItemResponse create(CreateItemRequest request) {
        CategoryEntity category = findCategory(request.categoryId());
        ItemEntity saved = itemRepository.save(new ItemEntity(
                request.name().trim(),
                request.price(),
                request.inStock(),
                category
        ));
        return mapper.toItemResponse(saved);
    }

    public ItemResponse update(Long id, UpdateItemRequest request) {
        ItemEntity item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        item.setName(request.name().trim());
        item.setPrice(request.price());
        item.setInStock(request.inStock());
        item.setCategory(findCategory(request.categoryId()));
        return mapper.toItemResponse(item);
    }

    private CategoryEntity findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }
}
