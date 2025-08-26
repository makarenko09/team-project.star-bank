package org.skypro.star.service;

import lombok.extern.slf4j.Slf4j;
import org.skypro.star.model.DynamicRule;
import org.skypro.star.model.QueryTypeDynamicRule;
import org.skypro.star.model.Recommendation;
import org.skypro.star.model.mapper.RecommendationMapper;
import org.skypro.star.repository.RecommendationRepository;
import org.skypro.star.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DynamicRuleHandler {
    private final RecommendationRepository recommendationRepository;
    private final TransactionRepository transactionRepository;
    private final RecommendationMapper recommendationMapper;

    public DynamicRuleHandler(RecommendationRepository recommendationRepository, TransactionRepository transactionRepository, RecommendationMapper recommendationMapper) {
        this.recommendationRepository = recommendationRepository;
        this.transactionRepository = transactionRepository;
        this.recommendationMapper = recommendationMapper;
    }

//    private final Logger log = LoggerFactory.getLogger(DynamicRuleHandler.class);

    private boolean handleAnyDynamicRule(UUID userUUID, DynamicRule dynamicRule) {
        try {
            QueryTypeDynamicRule getQueryTypeDynamicRule = QueryTypeDynamicRule.valueOf(dynamicRule.getQuery());
            return getQueryTypeDynamicRule.checkRule(userUUID, transactionRepository, dynamicRule);

        } catch (IllegalArgumentException e) {
            log.error("Enum error in handleAnyDynamicRule({}): ", e.getMessage());
            throw e;
        }
    }

    /// Like a {@link RecommendationRuleSetImpl#handlerOverlap}
    /// Also, u can try to make this method via Enum {@linkplain QueryTypeDynamicRule} or {@link List#iterator() .iterate() of DynamicRule}
    ///
    /// @see <a href="https://www.geeksforgeeks.org/java/stream-allmatch-java-examples/">usage: .allMatch</a>
    /// @see <a href="https://quickref.me/java#:~:text=for%20(int%20i%20%3D%200%3B%20i%20%3C%205%3B%20i%2B%2B)%20%7B%0A%20%20if%20(i%20%3D%3D%203)%20%7B%0A%20%20%20%20continue%3B%0A%20%20%7D%0A%20%20System.out.print(i)%3B%0A%7D%0A//%20Outputs%3A%2001245">if (...) continue </a>
    public List<Recommendation> handleQueryTypeDynamicRule(UUID userUUID, List<Recommendation> localRecommendationAnswerUser) {
        Set<UUID> uuidListWithoutLocalRecommendationAnswerUser = severanceFromLocalRuleInCode(localRecommendationAnswerUser);

        log.debug("Load data after clean from local recommendation answer: {}", uuidListWithoutLocalRecommendationAnswerUser.toString());
        uuidListWithoutLocalRecommendationAnswerUser.stream().filter(id -> {

                    List<DynamicRule> rules = recommendationRepository.getDynamicRulesByIdFromJSONB(id);

                    if (!recommendationRepository.checkContainDynamicRules(rules)) {
                        return false;
                    }

                    return rules.stream().allMatch(dr -> {
                        return handleAnyDynamicRule(userUUID, dr);

                    });
                })
                .collect(Collectors.toList());

        return uuidListWithoutLocalRecommendationAnswerUser.stream()
                .map(recommendationRepository::getRecommendation)
                .collect(Collectors.toList());
    }

    private Set<UUID> severanceFromLocalRuleInCode(List<Recommendation> localRecommendationAnswerUser) {

        Set<UUID> uuidListFromFirstRecommendationAnswerUser = localRecommendationAnswerUser.stream()
                .map(Recommendation::getId)
                .collect(Collectors.toSet());
        List<UUID> allIdDynamicRules = recommendationRepository.getAllIdDynamicRules();

        Set<UUID> filtered = allIdDynamicRules.stream().filter(idRule -> !uuidListFromFirstRecommendationAnswerUser.contains(idRule)).collect(Collectors.toSet());
        return filtered;
    }
}
