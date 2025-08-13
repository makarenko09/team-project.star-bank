package org.skypro.star.controller;

import org.skypro.star.model.*;
import org.skypro.star.service.RecommendationRuleSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/recommendation")
public class RecommendationController {
    private final RecommendationRuleSetImpl recommendationRuleSet;

    private final Logger logger = LoggerFactory.getLogger(RecommendationController.class);


    public RecommendationController(RecommendationRuleSetImpl recommendationRuleSet) {
        this.recommendationRuleSet = recommendationRuleSet;

    }

    @GetMapping("/{userId}")
    public RecommendationAnswerUser getRecommendation(@PathVariable(name = "userId") UUID userId) {
        return recommendationRuleSet.getRecommendation(UUID.fromString(userId.toString()));
    }

    @PostMapping("/rule")
    public RecommendationAnswerDynamicRule createDynamicRule(@RequestBody RecommendationWithDynamicRule recommendationWithDynamicRule) {
        return recommendationRuleSet.insertData(recommendationWithDynamicRule);
    }

    @GetMapping("/rule")
    public RecommendationsAnswerDynamicRule getAllDynamicRules() {
        return recommendationRuleSet.getData();
    }

    @GetMapping("/getDynamicRulesFromDynamicRecommendation")
    public List<DynamicRule> getDynamicRules(UUID ruleUUID) {
        return recommendationRuleSet.getJSONB(ruleUUID);
    }

}
