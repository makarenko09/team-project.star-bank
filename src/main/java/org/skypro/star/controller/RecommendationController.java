package org.skypro.star.controller;

import org.skypro.star.model.RecommendationAnswer;
import org.skypro.star.model.RecommendationWithDynamicRule;
import org.skypro.star.repository.TransactionRepository;
import org.skypro.star.service.RecommendationRuleSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/recommendation")
public class RecommendationController {
    private final RecommendationRuleSetImpl recommendationRuleSet;

    private final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    public RecommendationController(RecommendationRuleSetImpl recommendationRuleSet) {
        this.recommendationRuleSet = recommendationRuleSet;
    }

    @PostMapping("/rule")
    public void createDynamicRule(@RequestBody RecommendationWithDynamicRule recommendationWithDynamicRule) {
        recommendationRuleSet.insertData(recommendationWithDynamicRule);
    }

    @GetMapping("/{user_id}")
    public RecommendationAnswer getRecommendation(@PathVariable(name = "user_id") UUID user_id) {
        return recommendationRuleSet.getRecommendation(UUID.fromString(user_id.toString()));
    }
}
