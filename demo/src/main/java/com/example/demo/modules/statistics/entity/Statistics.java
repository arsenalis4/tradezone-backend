package com.example.demo.modules.statistics.entity;

import com.example.demo.modules.statistics.enums.StatisticType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatisticType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long value;

    @Column(nullable = false)
    private LocalDate date;

    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
} 