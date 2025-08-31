package org.skypro.star.controller;

import org.skypro.star.service.RecommendationService;
import org.skypro.star.service.RuleManagementService;
import org.skypro.star.service.StatisticsService;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final CacheManager cacheManager;
    private final InfoEndpoint infoEndpoint;
    private final RecommendationService recommendationService;
    private final RuleManagementService ruleManagementService;
    private final StatisticsService statisticsService;

    public ManagementController(CacheManager cacheManager,
                                InfoEndpoint infoEndpoint,
                                RecommendationService recommendationService,
                                RuleManagementService ruleManagementService,
                                StatisticsService statisticsService) {
        this.cacheManager = cacheManager;
        this.infoEndpoint = infoEndpoint;
        this.recommendationService = recommendationService;
        this.ruleManagementService = ruleManagementService;
        this.statisticsService = statisticsService;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = infoEndpoint.info();
        @SuppressWarnings("unchecked")
        Map<String, Object> buildInfo = (Map<String, Object>) info.get("build");

        return ResponseEntity.ok(Map.of(
                "name", buildInfo.get("name"),
                "version", buildInfo.get("version")
        ));
    }
}