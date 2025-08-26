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
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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

    @Autowired
    private BuildProperties buildProperties;

    @GetMapping("/recommendation/{userId}")
    public RecommendationAnswerUser getRecommendation(@PathVariable(name = "userId") UUID userId) {
        return recommendationRuleSet.getRecommendation(UUID.fromString(userId.toString()));
    }
    @GetMapping("/management/info")
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("name", buildProperties.getName());
        info.put("version", buildProperties.getVersion());
        return info;
    }

    @PostMapping("/management/clear-caches")
    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName ->
                cacheManager.getCache(cacheName).clear()
        );
    }

    @GetMapping("/rule/stats")
    public StatsUsageGetRecommendationByUser getStatsUsageGetRecommendationByUser() {
                return recommendationRuleSet.getStatsUsageGetRecommendationByUser();
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
