package org.skypro.star.service;

import org.skypro.star.model.DTO.RuleStatsResponse;
import org.skypro.star.model.RuleStatistic;
import org.skypro.star.repository.RecommendationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final RecommendationRepository recommendationRepository;

    public StatisticsService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    public RuleStatsResponse getRuleStats() {
        List<RuleStatistic> stats = recommendationRepository.getAllRuleStats();
        return new RuleStatsResponse(stats);
    }
}