package org.skypro.star.service;

import org.skypro.star.model.RecommendationAnswerDynamicRule;
import org.skypro.star.model.RecommendationWithDynamicRule;
import org.skypro.star.model.RecommendationsAnswerDynamicRule;
import org.skypro.star.repository.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RuleManagementService {

    private final RecommendationRepository recommendationRepository;
    private final Logger logger = LoggerFactory.getLogger(RuleManagementService.class);

    public RuleManagementService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    public RecommendationAnswerDynamicRule createDynamicRule(RecommendationWithDynamicRule recommendationWithDynamicRule) {
        logger.info("Creating dynamic rule: {}", recommendationWithDynamicRule);
        // Бизнес-логика создания правила
        boolean success = recommendationRepository.insertRecommendationWithQuery(
                recommendationWithDynamicRule.getId(),
                recommendationWithDynamicRule.getName(),
                List.of(recommendationWithDynamicRule.getDynamicRule()),
                recommendationWithDynamicRule.getText()
        );

        if (success) {
            Integer rowId = recommendationRepository.getRowNumberId(recommendationWithDynamicRule.getId());
            return new RecommendationAnswerDynamicRule(rowId, recommendationWithDynamicRule);
        }
        throw new RuntimeException("Failed to create dynamic rule");
    }

    public RecommendationsAnswerDynamicRule getAllDynamicRules() {
        logger.info("Getting all dynamic rules");
        // Логика получения всех правил
        return new RecommendationsAnswerDynamicRule(new RecommendationAnswerDynamicRule[0]);
    }

    public void deleteDynamicRule(UUID ruleId) {
        logger.info("Deleting dynamic rule: {}", ruleId);
        recommendationRepository.deleteRule(ruleId);
    }
}