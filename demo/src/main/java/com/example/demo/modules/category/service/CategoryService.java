package com.example.demo.modules.category.service;

import com.example.demo.modules.category.dto.CategoryDto;
import com.example.demo.modules.category.entity.Category;
import com.example.demo.modules.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryDto.Response createCategory(CategoryDto.CreateRequest request) {
        // 이름 중복 검사
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("이미 존재하는 카테고리 이름입니다: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .color(request.getColor())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("새 카테고리 생성됨: {}", savedCategory.getName());

        return modelMapper.map(savedCategory, CategoryDto.Response.class);
    }

    @Transactional(readOnly = true)
    public CategoryDto.Response getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + id));

        return modelMapper.map(category, CategoryDto.Response.class);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto.Response> getAllActiveCategories() {
        return categoryRepository.findAllActiveCategories().stream()
                .map(category -> modelMapper.map(category, CategoryDto.Response.class))
                .collect(Collectors.toList());
    }

    public CategoryDto.Response updateCategory(Long id, CategoryDto.UpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + id));

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new RuntimeException("이미 존재하는 카테고리 이름입니다: " + request.getName());
            }
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("카테고리 업데이트됨: {}", updatedCategory.getName());

        return modelMapper.map(updatedCategory, CategoryDto.Response.class);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + id));

        category.setIsActive(false);
        categoryRepository.save(category);

        log.info("카테고리 비활성화됨: {}", category.getName());
    }
} 