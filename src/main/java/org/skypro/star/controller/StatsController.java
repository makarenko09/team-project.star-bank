package org.skypro.star.controller;

import org.skypro.star.model.DTO.RuleStatsResponse;
import org.skypro.star.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatsController {

    private final StatisticsService statisticsService;

    public StatsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/rules")
    public ResponseEntity<RuleStatsResponse> getRuleStats() {
        return ResponseEntity.ok(statisticsService.getRuleStats());
    }
}