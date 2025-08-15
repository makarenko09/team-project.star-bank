package org.skypro.star.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RecommendationWithDynamicRule {
    @JsonProperty("product_name")
    private final String name;
    @JsonProperty("product_id")
    private final UUID id;
    @JsonProperty("product_text")
    private final String text;
    @JsonProperty("rule")
    @Getter
    DynamicRule[] dynamicRule;

}
