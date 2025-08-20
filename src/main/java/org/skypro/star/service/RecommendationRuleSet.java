package org.skypro.star.service;

import org.skypro.star.model.RecommendationAnswerUser;

import java.util.UUID;

public interface RecommendationRuleSet {
    RecommendationAnswerUser getRecommendation(UUID uuid);
}
