package org.skypro.star.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationsAnswerDynamicRule {
    @JsonProperty("data")
    @JsonUnwrapped
    private final RecommendationAnswerDynamicRule[] recommendationAnswerUser;
}
