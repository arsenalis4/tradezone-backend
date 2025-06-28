package com.example.demo.modules.category.controller;

import com.example.demo.modules.category.dto.CategoryDto;
import com.example.demo.modules.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category", description = "카테고리 관리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "카테고리 이름 중복")
    })
    @PostMapping
    public ResponseEntity<CategoryDto.Response> createCategory(@Valid @RequestBody CategoryDto.CreateRequest request) {
        try {
            CategoryDto.Response response = categoryService.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "카테고리 조회", description = "특정 카테고리를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto.Response> getCategoryById(@PathVariable Long id) {
        try {
            CategoryDto.Response response = categoryService.getCategoryById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "활성 카테고리 목록 조회", description = "모든 활성 카테고리 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CategoryDto.Response>> getAllActiveCategories() {
        List<CategoryDto.Response> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 정보를 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto.Response> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto.UpdateRequest request) {
        try {
            CategoryDto.Response response = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 비활성화합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 