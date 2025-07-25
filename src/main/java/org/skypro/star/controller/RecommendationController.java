package org.skypro.star.controller;

import org.skypro.star.model.RecommendationAnswerDynamicRule;
import org.skypro.star.model.RecommendationAnswerUser;
import org.skypro.star.model.RecommendationWithDynamicRule;
import org.skypro.star.service.RecommendationRuleSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    public RecommendationAnswerDynamicRule createDynamicRule(@RequestBody RecommendationWithDynamicRule recommendationWithDynamicRule) {
       return recommendationRuleSet.insertData(recommendationWithDynamicRule);
    }

    @GetMapping("/{user_id}")
    public RecommendationAnswerUser getRecommendation(@PathVariable(name = "user_id") UUID user_id) {
        return recommendationRuleSet.getRecommendation(UUID.fromString(user_id.toString()));
    }
}
