package org.skypro.star.controller;

import org.skypro.star.model.RecommendationAnswerDynamicRule;
import org.skypro.star.model.RecommendationAnswerUser;
import org.skypro.star.model.RecommendationWithDynamicRule;
import org.skypro.star.model.RecommendationsAnswerDynamicRule;
import org.skypro.star.model.stat.StatsUsageGetRecommendationByUser;
import org.skypro.star.service.RecommendationRuleSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class RecommendationController {
    private final RecommendationRuleSetImpl recommendationRuleSet;

    private final CacheManager cacheManager;
    private final Logger logger = LoggerFactory.getLogger(RecommendationController.class);


    public RecommendationController(RecommendationRuleSetImpl recommendationRuleSet, CacheManager cacheManager) {
        this.recommendationRuleSet = recommendationRuleSet;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/recommendation/{userId}")
    public RecommendationAnswerUser getRecommendation(@PathVariable(name = "userId") UUID userId) {
        return recommendationRuleSet.getRecommendation(UUID.fromString(userId.toString()));
    }
    //FIXME - write transfer increment with java.star.model.stat
    @GetMapping("/rule/stats")
    public StatsUsageGetRecommendationByUser getStatsUsageGetRecommendationByUser() {
                return recommendationRuleSet.getStatsUsageGetRecommendationByUser();
    }

    @PostMapping("/management/clear-caches")
    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName ->
                cacheManager.getCache(cacheName).clear()
        );
    }

    @PostMapping("/rule")
    public RecommendationAnswerDynamicRule createDynamicRule(@RequestBody RecommendationWithDynamicRule recommendationWithDynamicRule) {
        return recommendationRuleSet.insertData(recommendationWithDynamicRule);
    }


    @GetMapping("/rule")
    public RecommendationsAnswerDynamicRule getAllDynamicRules() {
        long start = System.currentTimeMillis();
        RecommendationsAnswerDynamicRule data = recommendationRuleSet.getData();
        long duration = System.currentTimeMillis() - start;
        logger.info("⏱ calling (parent) method executed in {} ms", duration);
        return data;
    }

    @DeleteMapping("/rule")
    public void deleteDynamicRule(@RequestBody UUID ruleId) {
        recommendationRuleSet.deleteData(ruleId);
    }
}
