package com.bnb.app.application.service;

import com.bnb.app.application.dto.CategoryResponse;
import com.bnb.app.application.dto.CreateCategoryRequest;
import com.bnb.app.application.mapper.BnbMapper;
import com.bnb.app.domain.model.CategoryEntity;
import com.bnb.app.domain.repository.CategoryRepository;
import com.bnb.app.web.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BnbMapper mapper;

    public CategoryService(CategoryRepository categoryRepository, BnbMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(mapper::toCategoryResponse)
                .toList();
    }

    public CategoryResponse create(CreateCategoryRequest request) {
        categoryRepository.findByNameIgnoreCase(request.name().trim())
                .ifPresent(existing -> {
                    throw new ConflictException("Category already exists: " + request.name());
                });

        CategoryEntity saved = categoryRepository.save(new CategoryEntity(request.name().trim()));
        return mapper.toCategoryResponse(saved);
    }
}
