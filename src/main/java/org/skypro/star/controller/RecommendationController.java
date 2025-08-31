package org.skypro.star.controller;

import org.skypro.star.model.RecommendationAnswerDynamicRule;
import org.skypro.star.model.RecommendationAnswerUser;
import org.skypro.star.model.RecommendationWithDynamicRule;
import org.skypro.star.model.RecommendationsAnswerDynamicRule;
import org.skypro.star.service.RecommendationService;
import org.skypro.star.service.RuleManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RuleManagementService ruleManagementService;
    private final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    public RecommendationController(RecommendationService recommendationService,
                                    RuleManagementService ruleManagementService) {
        this.recommendationService = recommendationService;
        this.ruleManagementService = ruleManagementService;
    }

    @GetMapping("/{userId}")
    public RecommendationAnswerUser getRecommendation(@PathVariable UUID userId) {
        return recommendationService.getRecommendation(userId);
    }

    @PostMapping("/rules")
    public RecommendationAnswerDynamicRule createDynamicRule(@RequestBody RecommendationWithDynamicRule recommendationWithDynamicRule) {
        return ruleManagementService.createDynamicRule(recommendationWithDynamicRule);
    }

    @GetMapping("/rules")
    public RecommendationsAnswerDynamicRule getAllDynamicRules() {
        return ruleManagementService.getAllDynamicRules();
    }

    @DeleteMapping("/rules/{ruleId}")
    public void deleteDynamicRule(@PathVariable UUID ruleId) {
        ruleManagementService.deleteDynamicRule(ruleId);
    }
}