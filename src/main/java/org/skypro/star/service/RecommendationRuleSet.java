package org.skypro.star.service;

import org.skypro.star.model.RecommendationAnswer;

import java.util.UUID;

public interface RecommendationRuleSet {
    RecommendationAnswer getRecommendation(UUID uuid);
}
