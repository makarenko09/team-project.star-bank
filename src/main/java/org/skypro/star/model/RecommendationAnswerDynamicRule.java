package org.skypro.star.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;

public record RecommendationAnswerDynamicRule(@JsonProperty("id") Integer id,
                                              @JsonUnwrapped RecommendationWithDynamicRule recommendationWithDynamicRule) {
}
