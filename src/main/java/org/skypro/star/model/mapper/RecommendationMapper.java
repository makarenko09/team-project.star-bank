package org.skypro.star.model.mapper;

import org.skypro.star.model.DynamicRule;
import org.skypro.star.model.Recommendation;
import org.skypro.star.model.RecommendationAnswerDynamicRule;
import org.skypro.star.model.RecommendationWithDynamicRule;
import org.skypro.star.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class RecommendationMapper {
    private final RecommendationRepository recommendationRepository;

    @Autowired
    public RecommendationMapper(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    public Function<Recommendation, RecommendationWithDynamicRule> fromRecommendationToRecommendationWithDynamicRule() {
        return recommendation -> {
            List<DynamicRule> dynamicRules = recommendationRepository.getDynamicRulesByIdFromJSONB(recommendation.getId());
//            Object[] array = dynamicRules.toArray();
            DynamicRule[] arrDynamicRules = Arrays.copyOf(dynamicRules.toArray(), dynamicRules.size(), DynamicRule[].class);
            return new RecommendationWithDynamicRule(
                    recommendation.getName(),
                    recommendation.getId(),
                    recommendation.getText(),
                    arrDynamicRules
            );
        };
    }

    public Function<RecommendationWithDynamicRule, RecommendationAnswerDynamicRule> fromRecommendationWithDynamicRuleToRecommendationAnswerDynamicRule() {
        return r -> {
            Integer rowNumberId = recommendationRepository.getRowNumberId(r.getId());
            return new RecommendationAnswerDynamicRule(rowNumberId, r);
        };
    }

}
