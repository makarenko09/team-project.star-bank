package org.skypro.star.controller;

import org.skypro.star.service.RecommendationRuleSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

public class ManagementController {
    private final RecommendationRuleSetImpl recommendationRuleSet;
    private final CacheManager cacheManager;
    private final Logger logger = LoggerFactory.getLogger(ManagementController.class);


    public ManagementController(RecommendationRuleSetImpl recommendationRuleSet, CacheManager cacheManager, BuildProperties buildProperties) {
        this.recommendationRuleSet = recommendationRuleSet;
        this.cacheManager = cacheManager;
        this.buildProperties = buildProperties;
    }


    private BuildProperties buildProperties;

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
}
