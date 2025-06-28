package com.example.demo.modules.statistics.repository;

import com.example.demo.modules.statistics.entity.Statistics;
import com.example.demo.modules.statistics.enums.StatisticType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    List<Statistics> findByTypeOrderByDateDesc(StatisticType type);

    List<Statistics> findByTypeAndDateBetweenOrderByDateDesc(StatisticType type, LocalDate startDate, LocalDate endDate);

    Optional<Statistics> findByTypeAndNameAndDate(StatisticType type, String name, LocalDate date);

    @Query("SELECT s FROM Statistics s WHERE s.type = :type AND s.date >= :startDate ORDER BY s.date ASC")
    List<Statistics> findByTypeAndDateAfterOrderByDateAsc(@Param("type") StatisticType type, @Param("startDate") LocalDate startDate);

    @Query("SELECT SUM(s.value) FROM Statistics s WHERE s.type = :type AND s.date = :date")
    Long sumValueByTypeAndDate(@Param("type") StatisticType type, @Param("date") LocalDate date);

    @Query("SELECT s FROM Statistics s WHERE s.date = :date ORDER BY s.value DESC")
    List<Statistics> findByDateOrderByValueDesc(@Param("date") LocalDate date);

    @Query("SELECT s FROM Statistics s WHERE s.type = :type ORDER BY s.value DESC LIMIT 10")
    List<Statistics> findTop10ByTypeOrderByValueDesc(@Param("type") StatisticType type);

    @Query("SELECT s.name as name, SUM(s.value) as totalValue FROM Statistics s " +
           "WHERE s.type = :type AND s.date BETWEEN :startDate AND :endDate " +
           "GROUP BY s.name ORDER BY totalValue DESC")
    List<Object[]> findAggregatedByTypeBetweenDates(
            @Param("type") StatisticType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
} 