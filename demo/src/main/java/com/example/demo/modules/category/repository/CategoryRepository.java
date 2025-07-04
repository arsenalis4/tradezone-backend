package com.example.demo.modules.category.repository;

import com.example.demo.modules.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findByIsActiveTrueOrderByName();

    @Query("SELECT c FROM Category c WHERE c.isActive = true")
    List<Category> findAllActiveCategories();

    @Query("SELECT COUNT(c) FROM Category c WHERE c.isActive = true")
    long countActiveCategories();
} 