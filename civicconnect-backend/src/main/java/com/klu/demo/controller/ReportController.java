package com.klu.demo.controller;

import com.klu.demo.model.Report;
import com.klu.demo.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitReport(@RequestBody Report report) {
        try {
            if (report.getText() == null || report.getCategory() == null
                    || report.getWardNumber() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Missing required fields"));
            }
            if ("High".equals(report.getPriority())) {
                report.setStatus("Urgent");
            } else {
                report.setStatus("Pending");
            }
            report.setVerified(false);
            Report saved = reportRepository.save(report);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to submit report: "
                            + e.getMessage()));
        }
    }

    @GetMapping("/my/{username}")
    public ResponseEntity<?> getMyReports(@PathVariable String username) {
        try {
            List<Report> reports = reportRepository.findByCreatedBy(username);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch reports"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReports() {
        try {
            return ResponseEntity.ok(reportRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch reports"));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            Optional<Report> opt = reportRepository.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Report not found"));
            }
            Report report = opt.get();
            if (body.containsKey("status"))   report.setStatus(body.get("status"));
            if (body.containsKey("verified")) report.setVerified(
                    Boolean.parseBoolean(body.get("verified")));
            reportRepository.save(report);
            return ResponseEntity.ok(Map.of("message", "Report updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Update failed"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteReport(@PathVariable Long id) {
        reportRepository.deleteById(id);
        return "Report deleted successfully";
    }

    @PutMapping("/feedback/{id}")
    public ResponseEntity<?> submitFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        try {
            Optional<Report> opt = reportRepository.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Report not found"));
            }
            Report report = opt.get();
            report.setFeedback(body.get("feedback"));
            reportRepository.save(report);
            return ResponseEntity.ok(Map.of("message", "Feedback saved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Feedback failed"));
        }
    }
}