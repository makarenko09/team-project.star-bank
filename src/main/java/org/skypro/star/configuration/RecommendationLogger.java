package org.skypro.star.configuration;

import org.skypro.star.model.Recommendation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RecommendationLogger {
    public static boolean checkFormat(Object arg) {
        return arg instanceof List<?> list &&
               list.stream().allMatch(Recommendation.class::isInstance);
    }

    public static Optional<String> convertFormatType(Object arg) {
        if (!checkFormat(arg)) {
            return Optional.empty();
        }

        return Optional.of("List<Recommendation>");
    }

    public static Optional<String> convertFormatArg(Object arg) {
        if (!checkFormat(arg)) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        List<Recommendation> recommendations = (List<Recommendation>) arg;
        String namesOnRecommendationsOfLogger = recommendations.stream()
                .map(Recommendation::getName)
                .collect(Collectors.joining(", ", "[", "...]"));


        return Optional.of(namesOnRecommendationsOfLogger);


    }
}

