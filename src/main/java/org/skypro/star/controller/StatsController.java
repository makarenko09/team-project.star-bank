package org.skypro.star.controller;

import org.skypro.star.model.DTO.RuleStatsResponse;
import org.skypro.star.model.RuleStatistic;
import org.skypro.star.service.RecommendationRuleSetImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatsController {

    private final RecommendationRuleSetImpl recommendationRuleSet;

    public StatsController(RecommendationRuleSetImpl recommendationRuleSet) {
        this.recommendationRuleSet = recommendationRuleSet;
    }

    @GetMapping("/rule/stats")
    public ResponseEntity<RuleStatsResponse> getRuleStats() {
        List<RuleStatistic> stats = recommendationRuleSet.getRuleStats();
        return ResponseEntity.ok(new RuleStatsResponse(stats));
    }
}