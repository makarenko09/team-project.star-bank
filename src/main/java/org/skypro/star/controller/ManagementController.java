package org.skypro.star.controller;

import org.skypro.star.model.RuleStatistic;
import org.skypro.star.service.RecommendationRuleSetImpl;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final RecommendationRuleSetImpl recommendationRuleSet;
    private final CacheManager cacheManager;

    public ManagementController(RecommendationRuleSetImpl recommendationRuleSet,
                                CacheManager cacheManager) {
        this.recommendationRuleSet = recommendationRuleSet;
        this.cacheManager = cacheManager;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "name", "STAR Recommendation Service",
                "version", "0.0.1-SNAPSHOT"
        ));
    }
}