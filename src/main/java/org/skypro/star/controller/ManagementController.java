package org.skypro.star.controller;

import org.skypro.star.service.RecommendationRuleSetImpl;
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
    private final RecommendationRuleSetImpl recommendationRuleSet;

    public ManagementController(CacheManager cacheManager,
                                InfoEndpoint infoEndpoint,
                                RecommendationRuleSetImpl recommendationRuleSet) {
        this.cacheManager = cacheManager;
        this.infoEndpoint = infoEndpoint;
        this.recommendationRuleSet = recommendationRuleSet;
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