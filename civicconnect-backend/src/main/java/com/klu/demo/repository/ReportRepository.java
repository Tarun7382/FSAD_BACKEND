package com.klu.demo.repository;

import com.klu.demo.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCreatedBy(String createdBy);   // citizen's own reports
    List<Report> findByStatus(String status);          // moderator filter
    List<Report> findByCategory(String category);      // filter by category
}